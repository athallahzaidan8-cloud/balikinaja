package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Data foto barang. File-nya ada di folder server, path-nya di database.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFoto;

    @Column(nullable = false, length = 100)
    private String namaFile;

    @Column(nullable = false, length = 255)
    private String path;

    @Column(length = 50)
    private String tipeFile;

    private long ukuran;

    protected Foto() {
    }

    public Foto(String namaFile, String path, String tipeFile, long ukuran) {
        this.namaFile = namaFile;
        this.path = path;
        this.tipeFile = tipeFile;
        this.ukuran = ukuran;
    }

    public String getUrl() {
        return "/uploads/" + namaFile;
    }

    public Long getIdFoto() {
        return idFoto;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public String getPath() {
        return path;
    }

    public String getTipeFile() {
        return tipeFile;
    }

    public long getUkuran() {
        return ukuran;
    }
}
