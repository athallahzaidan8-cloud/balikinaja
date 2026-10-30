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
 * Satu catatan perubahan status laporan: siapa, kapan, dari status apa ke status apa.
 * Fitur 6 - Status & Riwayat Laporan (PJ: Putra)
 */
@Entity
@Table(name = "riwayat_status")
public class RiwayatStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRiwayat;

    /** null untuk catatan pertama (laporan baru dibuat). */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusLaporan statusLama;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusLaporan statusBaru;

    @Column(nullable = false)
    private LocalDateTime waktu;

    @ManyToOne(optional = false)
    @JoinColumn(name = "diubah_oleh_id")
    private User diubahOleh;

    @Column(length = 300)
    private String keterangan;

    protected RiwayatStatus() {
    }

    public RiwayatStatus(StatusLaporan statusLama, StatusLaporan statusBaru, User diubahOleh, String keterangan) {
        this.statusLama = statusLama;
        this.statusBaru = statusBaru;
        this.diubahOleh = diubahOleh;
        this.keterangan = keterangan;
        this.waktu = LocalDateTime.now();
    }

    public String getDeskripsi() {
        if (statusLama == null) {
            return "Laporan dibuat dengan status " + statusBaru.getLabel();
        }
        return "Status berubah dari " + statusLama.getLabel() + " ke " + statusBaru.getLabel();
    }

    public Long getIdRiwayat() {
        return idRiwayat;
    }

    public StatusLaporan getStatusLama() {
        return statusLama;
    }

    public StatusLaporan getStatusBaru() {
        return statusBaru;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public User getDiubahOleh() {
        return diubahOleh;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
