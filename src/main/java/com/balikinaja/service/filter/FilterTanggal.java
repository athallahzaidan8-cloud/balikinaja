package com.balikinaja.service.filter;

import com.balikinaja.model.Laporan;
import java.time.LocalDate;

/**
 * Lolos bila tanggal kejadian berada di dalam rentang. Batas yang null berarti terbuka.
 * Fitur 4 - Cari & Filter Barang (PJ: Zazkia Maharrani Adzulvi)
 */
public class FilterTanggal implements FilterLaporan {

    private final LocalDate dari;
    private final LocalDate sampai;

    public FilterTanggal(LocalDate dari, LocalDate sampai) {
        this.dari = dari;
        this.sampai = sampai;
    }

    @Override
    public boolean cocok(Laporan laporan) {
        LocalDate tanggal = laporan.getTanggalKejadian();
        if (tanggal == null) {
            return false;
        }
        return (dari == null || !tanggal.isBefore(dari)) && (sampai == null || !tanggal.isAfter(sampai));
    }
}
