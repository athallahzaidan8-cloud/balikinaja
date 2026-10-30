package com.balikinaja.model;

/**
 * Kategori barang untuk laporan dan filter.
 * Fitur 2 - Lapor Barang Hilang (PJ: Raja Pandya Danendra Algesta)
 */
public enum Kategori {
    ELEKTRONIK("Elektronik"),
    DOKUMEN_KARTU("Dokumen & Kartu"),
    AKSESORIS("Aksesoris"),
    PAKAIAN("Pakaian"),
    LAINNYA("Lainnya");

    private final String label;

    Kategori(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
