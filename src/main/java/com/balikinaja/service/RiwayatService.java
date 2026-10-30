package com.balikinaja.service;

import com.balikinaja.model.Admin;
import com.balikinaja.model.Klaim;
import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.RiwayatStatus;
import com.balikinaja.model.StatusKlaim;
import com.balikinaja.model.StatusLaporan;
import com.balikinaja.model.User;
import com.balikinaja.repository.KlaimRepository;
import com.balikinaja.repository.LaporanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Memperbarui status laporan sesuai aturan dan menyediakan data riwayat.
 * Fitur 6 - Status & Riwayat Laporan (PJ: Putra)
 */
@Service
public class RiwayatService {

    private final LaporanRepository laporanRepo;
    private final KlaimRepository klaimRepo;

    public RiwayatService(LaporanRepository laporanRepo, KlaimRepository klaimRepo) {
        this.laporanRepo = laporanRepo;
        this.klaimRepo = klaimRepo;
    }

    @Transactional
    public void perbaruiStatus(Laporan laporan, StatusLaporan baru, User oleh, String ket) {
        laporan.ubahStatus(baru, oleh, ket);
        laporanRepo.save(laporan);
    }

    /**
     * Admin mencatat bahwa barang sudah diserahkan. Laporan pasangannya
     * (dari klaim yang disetujui) ikut ditandai selesai.
     */
    @Transactional
    public void tandaiSelesai(Long idLaporan, Admin admin) {
        Laporan laporan = getLaporan(idLaporan);
        if (laporan.getStatus() != StatusLaporan.DIKLAIM) {
            throw new IllegalStateException("Hanya laporan berstatus Diklaim yang bisa ditandai selesai.");
        }
        String ket = "Barang sudah diserahkan kepada pemiliknya";
        perbaruiStatus(laporan, StatusLaporan.SELESAI, admin, ket);
        for (Laporan pasangan : getLaporanPasangan(laporan)) {
            if (pasangan.getStatus() == StatusLaporan.DIKLAIM) {
                perbaruiStatus(pasangan, StatusLaporan.SELESAI, admin, ket);
            }
        }
    }

    public List<RiwayatStatus> getRiwayatLaporan(Long idLaporan) {
        return getLaporan(idLaporan).getRiwayat();
    }

    public List<Laporan> getLaporanPengguna(Pengguna pelapor) {
        return laporanRepo.findByPelaporOrderByIdLaporanDesc(pelapor);
    }

    public List<Laporan> getLaporanMenungguSerahTerima() {
        return laporanRepo.findByStatusOrderByIdLaporanDesc(StatusLaporan.DIKLAIM);
    }

    public List<Laporan> getSemuaLaporan(StatusLaporan status) {
        return status == null ? laporanRepo.findAllByOrderByIdLaporanDesc()
                : laporanRepo.findByStatusOrderByIdLaporanDesc(status);
    }

    public long hitungLaporan(StatusLaporan status) {
        return laporanRepo.countByStatus(status);
    }

    private Laporan getLaporan(Long idLaporan) {
        return laporanRepo.findById(idLaporan)
                .orElseThrow(() -> new IllegalArgumentException("Laporan tidak ditemukan."));
    }

    private List<Laporan> getLaporanPasangan(Laporan laporan) {
        List<Klaim> disetujui;
        if (laporan instanceof LaporanTemuan temuan) {
            disetujui = klaimRepo.findByLaporanTemuanAndStatus(temuan, StatusKlaim.DISETUJUI);
            return disetujui.stream().map(Klaim::getLaporanKehilangan)
                    .filter(l -> l != null).map(Laporan.class::cast).toList();
        }
        disetujui = klaimRepo.findByLaporanKehilanganAndStatus((LaporanKehilangan) laporan, StatusKlaim.DISETUJUI);
        return disetujui.stream().map(Klaim::getLaporanTemuan).map(Laporan.class::cast).toList();
    }
}
