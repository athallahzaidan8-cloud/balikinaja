package com.balikinaja.service.filter;

import com.balikinaja.model.Laporan;
import java.util.Locale;

/**
 * Lolos bila lokasi laporan mengandung kata lokasi yang dicari (tidak peka huruf besar/kecil).
 * Fitur 4 - Cari & Filter Barang (PJ: Zazkia Maharrani Adzulvi)
 */
public class FilterLokasi implements FilterLaporan {

    private final String kataLokasi;

    public FilterLokasi(String kataLokasi) {
        this.kataLokasi = kataLokasi.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean cocok(Laporan laporan) {
        return laporan.getLokasi() != null
                && laporan.getLokasi().toLowerCase(Locale.ROOT).contains(kataLokasi);
    }
}
