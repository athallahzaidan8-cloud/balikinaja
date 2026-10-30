package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

/**
 * Laporan dari orang yang kehilangan barang. Status awal: DICARI.
 * Fitur 2 - Lapor Barang Hilang (PJ: Raja Pandya Danendra Algesta)
 */
@Entity
@DiscriminatorValue("KEHILANGAN")
public class LaporanKehilangan extends Laporan {

    @Column(length = 500)
    private String ciriKhusus;

    @Column(length = 50)
    private String kontakAlternatif;

    /** dipakai JPA dan binding form */
    public LaporanKehilangan() {
    }

    public LaporanKehilangan(String namaBarang, Kategori kategori, String deskripsi, String lokasi,
                             LocalDate tanggalKejadian, Pengguna pelapor, String ciriKhusus) {
        super(namaBarang, kategori, deskripsi, lokasi, tanggalKejadian, pelapor);
        this.ciriKhusus = ciriKhusus;
    }

    public void perbaruiDari(LaporanKehilangan data) {
        salinDataUmum(data);
        this.ciriKhusus = data.ciriKhusus;
        this.kontakAlternatif = data.kontakAlternatif;
    }

    @Override
    public String getJenisLaporan() {
        return "Kehilangan";
    }

    @Override
    public StatusLaporan getStatusAwal() {
        return StatusLaporan.DICARI;
    }

    @Override
    public boolean validasi() {
        return validasiDataUmum();
    }

    public String getCiriKhusus() {
        return ciriKhusus;
    }

    public void setCiriKhusus(String ciriKhusus) {
        this.ciriKhusus = ciriKhusus;
    }

    public String getKontakAlternatif() {
        return kontakAlternatif;
    }

    public void setKontakAlternatif(String kontakAlternatif) {
        this.kontakAlternatif = kontakAlternatif;
    }
}
