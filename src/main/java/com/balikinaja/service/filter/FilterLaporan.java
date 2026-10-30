package com.balikinaja.service.filter;

import com.balikinaja.model.Laporan;

/**
 * Kontrak satu kriteria penyaringan laporan (Strategy pattern).
 * Fitur 4 - Cari & Filter Barang (PJ: Zazkia Maharrani Adzulvi)
 */
public interface FilterLaporan {

    boolean cocok(Laporan laporan);
}
