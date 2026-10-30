package com.balikinaja.service;

import com.balikinaja.model.Foto;
import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.StatusLaporan;
import com.balikinaja.repository.LaporanRepository;
import com.balikinaja.service.storage.PenyimpananFoto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Membuat dan mengubah laporan temuan, serta menyediakan daftar barang yang masih bisa diklaim.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@Service
public class LaporanTemuanService {

    private final LaporanRepository laporanRepo;
    private final PenyimpananFoto penyimpananFoto;

    public LaporanTemuanService(LaporanRepository laporanRepo, PenyimpananFoto penyimpananFoto) {
        this.laporanRepo = laporanRepo;
        this.penyimpananFoto = penyimpananFoto;
    }

    @Transactional
    public LaporanTemuan buatLaporan(Pengguna pelapor, LaporanTemuan data, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Foto barang wajib diunggah.");
        }
        data.setPelapor(pelapor);
        Foto foto = penyimpananFoto.simpan(file);
        data.setFoto(foto);
        if (!data.validasi()) {
            penyimpananFoto.hapus(foto);
            throw new IllegalArgumentException("Data laporan belum lengkap. Pastikan lokasi penyimpanan, "
                    + "pertanyaan, dan jawaban verifikasi terisi, serta tanggal tidak melebihi hari ini.");
        }
        data.ubahStatus(data.getStatusAwal(), pelapor, "Laporan temuan dibuat");
        return laporanRepo.save(data);
    }

    @Transactional
    public LaporanTemuan editLaporan(Long id, LaporanTemuan data, Pengguna pelapor, MultipartFile fotoBaru) {
        LaporanTemuan laporan = getLaporanMilik(id, pelapor);
        if (laporan.getStatus() != StatusLaporan.DITEMUKAN) {
            throw new IllegalStateException("Laporan temuan yang sudah diklaim tidak dapat diubah.");
        }
        laporan.perbaruiDari(data);
        Foto fotoLama = laporan.getFoto();
        if (fotoBaru != null && !fotoBaru.isEmpty()) {
            laporan.setFoto(penyimpananFoto.simpan(fotoBaru));
        }
        if (!laporan.validasi()) {
            if (laporan.getFoto() != fotoLama) {
                penyimpananFoto.hapus(laporan.getFoto());
            }
            throw new IllegalArgumentException("Data laporan belum lengkap, atau tanggal kejadian melebihi hari ini.");
        }
        if (laporan.getFoto() != fotoLama) {
            penyimpananFoto.hapus(fotoLama);
        }
        return laporanRepo.save(laporan);
    }

    public List<LaporanTemuan> getTemuanTersedia() {
        return laporanRepo.findTemuanByStatus(StatusLaporan.DITEMUKAN);
    }

    public LaporanTemuan getLaporanMilik(Long id, Pengguna pelapor) {
        Laporan laporan = laporanRepo.findById(id).orElse(null);
        if (!(laporan instanceof LaporanTemuan temuan) || !temuan.milik(pelapor)) {
            throw new IllegalArgumentException("Laporan temuan tidak ditemukan.");
        }
        return temuan;
    }
}
