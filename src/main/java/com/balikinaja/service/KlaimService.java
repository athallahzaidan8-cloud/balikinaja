package com.balikinaja.service;

import com.balikinaja.model.Admin;
import com.balikinaja.model.Klaim;
import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.StatusKlaim;
import com.balikinaja.model.StatusLaporan;
import com.balikinaja.repository.KlaimRepository;
import com.balikinaja.repository.LaporanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pengajuan klaim oleh pemilik dan verifikasi oleh Admin.
 * Fitur 5 - Klaim Barang & Verifikasi Admin (PJ: Atika Supri Nandya)
 */
@Service
public class KlaimService {

    private final KlaimRepository klaimRepo;
    private final LaporanRepository laporanRepo;
    private final RiwayatService riwayatService;

    public KlaimService(KlaimRepository klaimRepo, LaporanRepository laporanRepo, RiwayatService riwayatService) {
        this.klaimRepo = klaimRepo;
        this.laporanRepo = laporanRepo;
        this.riwayatService = riwayatService;
    }

    /** idKehilangan opsional: laporan kehilangan milik pengklaim yang ingin ditautkan. */
    @Transactional
    public Klaim ajukanKlaim(Pengguna pengklaim, Long idTemuan, String jawaban, Long idKehilangan) {
        Laporan laporan = laporanRepo.findById(idTemuan).orElse(null);
        if (!(laporan instanceof LaporanTemuan temuan)) {
            throw new IllegalArgumentException("Barang temuan tidak ditemukan.");
        }
        if (!temuan.bisaDiklaim()) {
            throw new IllegalStateException("Barang ini sedang tidak bisa diklaim.");
        }
        if (temuan.milik(pengklaim)) {
            throw new IllegalStateException("Anda tidak dapat mengklaim barang yang Anda laporkan sendiri.");
        }
        if (jawaban == null || jawaban.isBlank()) {
            throw new IllegalArgumentException("Jawaban pertanyaan verifikasi wajib diisi.");
        }
        if (cekKlaimGanda(pengklaim, temuan)) {
            throw new IllegalStateException("Anda sudah mengajukan klaim untuk barang ini.");
        }
        Klaim klaim = new Klaim(pengklaim, temuan, jawaban.trim());
        if (idKehilangan != null) {
            LaporanKehilangan kehilangan = getKehilanganMilik(idKehilangan, pengklaim);
            klaim.setLaporanKehilangan(kehilangan);
            riwayatService.perbaruiStatus(kehilangan, StatusLaporan.DITEMUKAN, pengklaim,
                    "Pemilik mengajukan klaim atas barang temuan #" + temuan.getIdLaporan());
        }
        return klaimRepo.save(klaim);
    }

    public List<Klaim> getKlaimMenunggu() {
        return klaimRepo.findByStatusOrderByTanggalPengajuanAsc(StatusKlaim.MENUNGGU);
    }

    @Transactional
    public Klaim verifikasi(Long idKlaim, Admin admin, boolean disetujui, String catatan) {
        Klaim klaim = getKlaim(idKlaim);
        if (disetujui) {
            setujui(klaim, admin, catatan);
        } else {
            tolak(klaim, admin, catatan == null || catatan.isBlank() ? "Jawaban verifikasi tidak sesuai." : catatan);
        }
        return klaimRepo.save(klaim);
    }

    public List<Klaim> getKlaimSaya(Pengguna pengklaim) {
        return klaimRepo.findByPengklaimOrderByTanggalPengajuanDesc(pengklaim);
    }

    public List<Klaim> getKlaimUntukTemuan(LaporanTemuan temuan) {
        return klaimRepo.findByLaporanTemuanOrderByTanggalPengajuanDesc(temuan);
    }

    /** Laporan kehilangan pengklaim yang masih Dicari, untuk pilihan tautan di form klaim. */
    public List<LaporanKehilangan> getKehilanganBisaDitautkan(Pengguna pengklaim) {
        return laporanRepo.findKehilanganByPelaporAndStatus(pengklaim, StatusLaporan.DICARI);
    }

    public long hitungKlaimMenunggu() {
        return klaimRepo.countByStatus(StatusKlaim.MENUNGGU);
    }

    public Klaim getKlaim(Long idKlaim) {
        return klaimRepo.findById(idKlaim).orElseThrow(() -> new IllegalArgumentException("Klaim tidak ditemukan."));
    }

    private void setujui(Klaim klaim, Admin admin, String catatan) {
        klaim.setujui(admin, catatan);
        LaporanTemuan temuan = klaim.getLaporanTemuan();
        riwayatService.perbaruiStatus(temuan, StatusLaporan.DIKLAIM, admin,
                "Klaim disetujui. Barang menunggu diambil oleh " + klaim.getPengklaim().getNama());
        if (klaim.getLaporanKehilangan() != null) {
            riwayatService.perbaruiStatus(klaim.getLaporanKehilangan(), StatusLaporan.DIKLAIM, admin,
                    "Klaim disetujui. Barang dapat diambil di " + temuan.getLokasiPenyimpanan());
        }
        // klaim lain atas barang yang sama otomatis ditolak
        for (Klaim lain : klaimRepo.findByLaporanTemuanAndStatus(temuan, StatusKlaim.MENUNGGU)) {
            if (!lain.getIdKlaim().equals(klaim.getIdKlaim())) {
                tolak(lain, admin, "Barang telah diklaim oleh pemilik lain.");
            }
        }
    }

    private void tolak(Klaim klaim, Admin admin, String alasan) {
        klaim.tolak(admin, alasan);
        LaporanKehilangan kehilangan = klaim.getLaporanKehilangan();
        if (kehilangan != null && kehilangan.getStatus() == StatusLaporan.DITEMUKAN) {
            riwayatService.perbaruiStatus(kehilangan, StatusLaporan.DICARI, admin,
                    "Klaim ditolak, barang kembali dicari");
        }
    }

    private boolean cekKlaimGanda(Pengguna pengklaim, LaporanTemuan temuan) {
        return klaimRepo.existsByPengklaimAndLaporanTemuanAndStatusIn(pengklaim, temuan,
                List.of(StatusKlaim.MENUNGGU, StatusKlaim.DISETUJUI));
    }

    private LaporanKehilangan getKehilanganMilik(Long id, Pengguna pengklaim) {
        Laporan laporan = laporanRepo.findById(id).orElse(null);
        if (!(laporan instanceof LaporanKehilangan kehilangan) || !kehilangan.milik(pengklaim)
                || kehilangan.getStatus() != StatusLaporan.DICARI) {
            throw new IllegalArgumentException("Laporan kehilangan yang dipilih tidak valid.");
        }
        return kehilangan;
    }
}
