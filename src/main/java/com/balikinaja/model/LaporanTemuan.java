package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Laporan dari orang yang menemukan barang. Status awal: DITEMUKAN.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy);
 * implementasi Klaimable dikerjakan bersama Fitur 5 (PJ: Atika Supri Nandya).
 */
@Entity
@DiscriminatorValue("TEMUAN")
public class LaporanTemuan extends Laporan implements Klaimable {

    @Column(length = 150)
    private String lokasiPenyimpanan;

    @Column(length = 200)
    private String pertanyaanVerifikasi;

    // sengaja tanpa getter: jawaban hanya bisa dicocokkan lewat periksaJawaban()
    @Column(length = 200)
    private String jawabanVerifikasi;

    /** dipakai JPA dan binding form */
    public LaporanTemuan() {
    }

    public LaporanTemuan(String namaBarang, Kategori kategori, String deskripsi, String lokasi,
                         LocalDate tanggalKejadian, Pengguna pelapor, String lokasiPenyimpanan,
                         String pertanyaanVerifikasi, String jawabanVerifikasi) {
        super(namaBarang, kategori, deskripsi, lokasi, tanggalKejadian, pelapor);
        this.lokasiPenyimpanan = lokasiPenyimpanan;
        this.pertanyaanVerifikasi = pertanyaanVerifikasi;
        this.jawabanVerifikasi = jawabanVerifikasi;
    }

    /** Jawaban verifikasi lama dipertahankan bila kolomnya dikosongkan saat edit. */
    public void perbaruiDari(LaporanTemuan data) {
        salinDataUmum(data);
        this.lokasiPenyimpanan = data.lokasiPenyimpanan;
        this.pertanyaanVerifikasi = data.pertanyaanVerifikasi;
        if (isi(data.jawabanVerifikasi)) {
            this.jawabanVerifikasi = data.jawabanVerifikasi;
        }
    }

    @Override
    public String getJenisLaporan() {
        return "Temuan";
    }

    @Override
    public StatusLaporan getStatusAwal() {
        return StatusLaporan.DITEMUKAN;
    }

    @Override
    public boolean validasi() {
        return validasiDataUmum() && isi(lokasiPenyimpanan)
                && isi(pertanyaanVerifikasi) && isi(jawabanVerifikasi);
    }

    @Override
    public String getPertanyaanVerifikasi() {
        return pertanyaanVerifikasi;
    }

    /** Cocok bila sama persis setelah huruf kecil semua dan spasi berlebih dibuang. */
    @Override
    public boolean periksaJawaban(String jawaban) {
        return jawaban != null && normalisasi(jawabanVerifikasi).equals(normalisasi(jawaban));
    }

    @Override
    public boolean bisaDiklaim() {
        return status == StatusLaporan.DITEMUKAN;
    }

    private static String normalisasi(String teks) {
        return teks == null ? "" : teks.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public String getLokasiPenyimpanan() {
        return lokasiPenyimpanan;
    }

    public void setLokasiPenyimpanan(String lokasiPenyimpanan) {
        this.lokasiPenyimpanan = lokasiPenyimpanan;
    }

    public void setPertanyaanVerifikasi(String pertanyaanVerifikasi) {
        this.pertanyaanVerifikasi = pertanyaanVerifikasi;
    }

    public void setJawabanVerifikasi(String jawabanVerifikasi) {
        this.jawabanVerifikasi = jawabanVerifikasi;
    }
}
