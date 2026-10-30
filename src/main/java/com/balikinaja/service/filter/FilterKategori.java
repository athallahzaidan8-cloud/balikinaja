package com.balikinaja.service.filter;

import com.balikinaja.model.Kategori;
import com.balikinaja.model.Laporan;

/**
 * Lolos bila kategori laporan sama dengan kategori yang dipilih.
 * Fitur 4 - Cari & Filter Barang (PJ: Zazkia Maharrani Adzulvi)
 */
public class FilterKategori implements FilterLaporan {

    private final Kategori kategori;

    public FilterKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    @Override
    public boolean cocok(Laporan laporan) {
        return laporan.getKategori() == kategori;
    }
}
