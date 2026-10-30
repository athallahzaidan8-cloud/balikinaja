package com.balikinaja.model;

/**
 * Siklus hidup laporan beserta aturan perpindahan status yang sah.
 * Fitur 6 - Status & Riwayat Laporan (PJ: Putra)
 */
public enum StatusLaporan {
    DICARI("Dicari"),
    DITEMUKAN("Ditemukan"),
    DIKLAIM("Diklaim"),
    SELESAI("Selesai");

    private final String label;

    StatusLaporan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * DICARI -> DITEMUKAN -> DIKLAIM -> SELESAI.
     * DITEMUKAN boleh kembali ke DICARI ketika klaim atas laporan kehilangan ditolak.
     */
    public boolean bisaBerubahKe(StatusLaporan baru) {
        return switch (this) {
            case DICARI -> baru == DITEMUKAN;
            case DITEMUKAN -> baru == DIKLAIM || baru == DICARI;
            case DIKLAIM -> baru == SELESAI;
            case SELESAI -> false;
        };
    }
}
