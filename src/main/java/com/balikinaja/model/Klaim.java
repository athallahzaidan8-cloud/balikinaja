package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Pengajuan klaim atas satu barang temuan, opsional ditautkan ke laporan kehilangan pengklaim.
 * Fitur 5 - Klaim Barang & Verifikasi Admin (PJ: Atika Supri Nandya)
 */
@Entity
@Table(name = "klaim")
public class Klaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKlaim;

    @ManyToOne(optional = false)
    @JoinColumn(name = "laporan_temuan_id")
    private LaporanTemuan laporanTemuan;

    @ManyToOne
    @JoinColumn(name = "laporan_kehilangan_id")
    private LaporanKehilangan laporanKehilangan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pengklaim_id")
    private Pengguna pengklaim;

    @Column(nullable = false, length = 500)
    private String jawabanCiri;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusKlaim status;

    @Column(nullable = false)
    private LocalDateTime tanggalPengajuan;

    @ManyToOne
    @JoinColumn(name = "verifikator_id")
    private Admin verifikator;

    @Column(length = 500)
    private String catatanAdmin;

    protected Klaim() {
    }

    public Klaim(Pengguna pengklaim, LaporanTemuan laporanTemuan, String jawabanCiri) {
        this.pengklaim = pengklaim;
        this.laporanTemuan = laporanTemuan;
        this.jawabanCiri = jawabanCiri;
        this.status = StatusKlaim.MENUNGGU;
        this.tanggalPengajuan = LocalDateTime.now();
    }

    public void setujui(Admin admin, String catatan) {
        tutup(admin, catatan, StatusKlaim.DISETUJUI);
    }

    public void tolak(Admin admin, String alasan) {
        tutup(admin, alasan, StatusKlaim.DITOLAK);
    }

    // status klaim hanya bisa diputuskan sekali, dan hanya lewat setujui()/tolak()
    private void tutup(Admin admin, String catatan, StatusKlaim hasil) {
        if (status != StatusKlaim.MENUNGGU) {
            throw new IllegalStateException("Klaim ini sudah diverifikasi sebelumnya.");
        }
        this.verifikator = admin;
        this.catatanAdmin = catatan;
        this.status = hasil;
    }

    /** Hasil pencocokan otomatis jawaban, membantu Admin mengambil keputusan. */
    public boolean isJawabanCocok() {
        return laporanTemuan.periksaJawaban(jawabanCiri);
    }

    public StatusKlaim getStatus() {
        return status;
    }

    public Long getIdKlaim() {
        return idKlaim;
    }

    public LaporanTemuan getLaporanTemuan() {
        return laporanTemuan;
    }

    public LaporanKehilangan getLaporanKehilangan() {
        return laporanKehilangan;
    }

    public void setLaporanKehilangan(LaporanKehilangan laporanKehilangan) {
        this.laporanKehilangan = laporanKehilangan;
    }

    public Pengguna getPengklaim() {
        return pengklaim;
    }

    public String getJawabanCiri() {
        return jawabanCiri;
    }

    public LocalDateTime getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public Admin getVerifikator() {
        return verifikator;
    }

    public String getCatatanAdmin() {
        return catatanAdmin;
    }
}
