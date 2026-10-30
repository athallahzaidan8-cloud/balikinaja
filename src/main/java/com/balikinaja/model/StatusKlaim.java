package com.balikinaja.model;

/**
 * Status proses verifikasi sebuah klaim.
 * Fitur 5 - Klaim Barang & Verifikasi Admin (PJ: Atika Supri Nandya)
 */
public enum StatusKlaim {
    MENUNGGU("Menunggu verifikasi"),
    DISETUJUI("Disetujui"),
    DITOLAK("Ditolak");

    private final String label;

    StatusKlaim(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
