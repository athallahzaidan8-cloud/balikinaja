package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Isi (byte) file foto saat foto disimpan di database, dipakai oleh PenyimpananFotoDatabase.
 * Dipisah dari Foto supaya daftar laporan tidak ikut memuat isi gambar.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@Entity
@Table(name = "isi_foto")
public class IsiFoto {

    @Id
    @Column(length = 100)
    private String namaFile;

    @Column(nullable = false, length = 50)
    private String tipeFile;

    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] isi;

    protected IsiFoto() {
    }

    public IsiFoto(String namaFile, String tipeFile, byte[] isi) {
        this.namaFile = namaFile;
        this.tipeFile = tipeFile;
        this.isi = isi;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public String getTipeFile() {
        return tipeFile;
    }

    public byte[] getIsi() {
        return isi;
    }
}
