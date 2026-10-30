package com.balikinaja.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas abstrak induk laporan kehilangan dan laporan temuan.
 * Fitur 2 - Lapor Barang Hilang (PJ: Raja Pandya Danendra Algesta);
 * method ubahStatus() milik Fitur 6 - Status & Riwayat Laporan (PJ: Putra).
 */
@Entity
@Table(name = "laporan")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "jenis")
public abstract class Laporan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long idLaporan;

    @Column(nullable = false, length = 100)
    protected String namaBarang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected Kategori kategori;

    @Column(nullable = false, length = 1000)
    protected String deskripsi;

    @Column(nullable = false, length = 150)
    protected String lokasi;

    @Column(nullable = false)
    protected LocalDate tanggalKejadian;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected StatusLaporan status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pelapor_id")
    protected Pengguna pelapor;

    // komposisi: foto ikut terhapus bersama laporannya
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "foto_id")
    protected Foto foto;

    // komposisi: riwayat status hanya ada di dalam laporannya
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "laporan_id")
    @OrderBy("waktu ASC")
    protected List<RiwayatStatus> riwayat = new ArrayList<>();

    protected Laporan() {
    }

    public Laporan(String namaBarang, Kategori kategori, String deskripsi, String lokasi,
                   LocalDate tanggalKejadian, Pengguna pelapor) {
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.tanggalKejadian = tanggalKejadian;
        this.pelapor = pelapor;
    }

    /**
     * Satu-satunya jalan untuk mengubah status: aturan perpindahan dicek oleh
     * StatusLaporan, lalu perubahan dicatat sebagai RiwayatStatus baru.
     */
    public void ubahStatus(StatusLaporan baru, User oleh, String ket) {
        if (status != null && !status.bisaBerubahKe(baru)) {
            throw new IllegalStateException(
                    "Status tidak dapat diubah dari " + status.getLabel() + " ke " + baru.getLabel() + ".");
        }
        riwayat.add(new RiwayatStatus(status, baru, oleh, ket));
        status = baru;
    }

    public String getRingkasan() {
        return namaBarang + " - " + kategori.getLabel() + ", " + lokasi;
    }

    /** Menyalin data umum dari objek hasil form saat laporan diedit. */
    protected void salinDataUmum(Laporan data) {
        this.namaBarang = data.namaBarang;
        this.kategori = data.kategori;
        this.deskripsi = data.deskripsi;
        this.lokasi = data.lokasi;
        this.tanggalKejadian = data.tanggalKejadian;
    }

    /** true bila semua data umum laporan terisi dan tanggal kejadian tidak di masa depan. */
    protected boolean validasiDataUmum() {
        return isi(namaBarang) && kategori != null && isi(deskripsi) && isi(lokasi)
                && tanggalKejadian != null && !tanggalKejadian.isAfter(LocalDate.now())
                && pelapor != null && foto != null;
    }

    protected static boolean isi(String teks) {
        return teks != null && !teks.isBlank();
    }

    public abstract String getJenisLaporan();

    public abstract StatusLaporan getStatusAwal();

    public abstract boolean validasi();

    // ---- getter & setter (setter dipakai untuk binding form) ----

    public Long getIdLaporan() {
        return idLaporan;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public LocalDate getTanggalKejadian() {
        return tanggalKejadian;
    }

    public void setTanggalKejadian(LocalDate tanggalKejadian) {
        this.tanggalKejadian = tanggalKejadian;
    }

    public StatusLaporan getStatus() {
        return status;
    }

    public Pengguna getPelapor() {
        return pelapor;
    }

    public void setPelapor(Pengguna pelapor) {
        this.pelapor = pelapor;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }

    public List<RiwayatStatus> getRiwayat() {
        return riwayat;
    }

    public boolean milik(User user) {
        return user != null && pelapor != null && pelapor.getIdUser().equals(user.getIdUser());
    }
}
