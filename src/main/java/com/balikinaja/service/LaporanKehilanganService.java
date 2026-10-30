package com.balikinaja.service;

import com.balikinaja.model.Foto;
import com.balikinaja.model.Klaim;
import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.StatusLaporan;
import com.balikinaja.repository.KlaimRepository;
import com.balikinaja.repository.LaporanRepository;
import com.balikinaja.service.storage.PenyimpananFoto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Membuat, mengubah, dan menghapus laporan kehilangan.
 * Fitur 2 - Lapor Barang Hilang (PJ: Raja Pandya Danendra Algesta)
 */
@Service
public class LaporanKehilanganService {

    private final LaporanRepository laporanRepo;
    private final KlaimRepository klaimRepo;
    private final PenyimpananFoto penyimpananFoto;

    public LaporanKehilanganService(LaporanRepository laporanRepo, KlaimRepository klaimRepo,
                                    PenyimpananFoto penyimpananFoto) {
        this.laporanRepo = laporanRepo;
        this.klaimRepo = klaimRepo;
        this.penyimpananFoto = penyimpananFoto;
    }

    @Transactional
    public LaporanKehilangan buatLaporan(Pengguna pelapor, LaporanKehilangan data, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Foto barang wajib diunggah.");
        }
        data.setPelapor(pelapor);
        Foto foto = penyimpananFoto.simpan(file);
        data.setFoto(foto);
        if (!data.validasi()) {
            penyimpananFoto.hapus(foto);
            throw new IllegalArgumentException(
                    "Data laporan belum lengkap, atau tanggal kejadian melebihi hari ini.");
        }
        data.ubahStatus(data.getStatusAwal(), pelapor, "Laporan kehilangan dibuat");
        return laporanRepo.save(data);
    }

    @Transactional
    public LaporanKehilangan editLaporan(Long id, LaporanKehilangan data, Pengguna pelapor, MultipartFile fotoBaru) {
        LaporanKehilangan laporan = getLaporanMilik(id, pelapor);
        if (laporan.getStatus() != StatusLaporan.DICARI) {
            throw new IllegalStateException("Laporan hanya bisa diubah selama statusnya masih Dicari.");
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
            throw new IllegalArgumentException(
                    "Data laporan belum lengkap, atau tanggal kejadian melebihi hari ini.");
        }
        if (laporan.getFoto() != fotoLama) {
            penyimpananFoto.hapus(fotoLama);
        }
        return laporanRepo.save(laporan);
    }

    @Transactional
    public void hapusLaporan(Long id, Pengguna pelapor) {
        LaporanKehilangan laporan = getLaporanMilik(id, pelapor);
        if (laporan.getStatus() != StatusLaporan.DICARI) {
            throw new IllegalStateException("Laporan hanya bisa dihapus selama statusnya masih Dicari.");
        }
        // klaim lama yang pernah ditautkan (dan sudah ditolak) dilepas dulu tautannya
        for (Klaim klaim : klaimRepo.findByLaporanKehilangan(laporan)) {
            klaim.setLaporanKehilangan(null);
        }
        penyimpananFoto.hapus(laporan.getFoto());
        laporanRepo.delete(laporan);
    }

    public List<LaporanKehilangan> getLaporanSaya(Pengguna pelapor) {
        return laporanRepo.findKehilanganByPelapor(pelapor);
    }

    public LaporanKehilangan getLaporanMilik(Long id, Pengguna pelapor) {
        Laporan laporan = laporanRepo.findById(id).orElse(null);
        if (!(laporan instanceof LaporanKehilangan kehilangan) || !kehilangan.milik(pelapor)) {
            throw new IllegalArgumentException("Laporan kehilangan tidak ditemukan.");
        }
        return kehilangan;
    }
}
