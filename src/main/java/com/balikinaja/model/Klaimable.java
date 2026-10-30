package com.balikinaja.model;

/**
 * Kemampuan "dapat diklaim" oleh pemiliknya. Diimplementasikan oleh LaporanTemuan.
 * Fitur 5 - Klaim Barang & Verifikasi Admin (PJ: Atika Supri Nandya)
 */
public interface Klaimable {

    String getPertanyaanVerifikasi();

    boolean periksaJawaban(String jawaban);

    boolean bisaDiklaim();
}
