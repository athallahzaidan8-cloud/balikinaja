package com.balikinaja.service;

import com.balikinaja.model.Laporan;
import com.balikinaja.model.StatusLaporan;
import com.balikinaja.repository.LaporanRepository;
import com.balikinaja.service.filter.FilterLaporan;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * Pencarian laporan: cocokkan kata kunci, lalu jalankan semua filter yang dipilih.
 * Fitur 4 - Cari & Filter Barang (PJ: Zazkia Maharrani Adzulvi)
 */
@Service
public class PencarianService {

    private final LaporanRepository laporanRepo;

    public PencarianService(LaporanRepository laporanRepo) {
        this.laporanRepo = laporanRepo;
    }

    /** Laporan berstatus SELESAI tidak ikut ditampilkan pada hasil pencarian. */
    public List<Laporan> cari(String kataKunci, List<FilterLaporan> filter) {
        List<Laporan> hasil = new ArrayList<>();
        for (Laporan laporan : laporanRepo.findAllByOrderByIdLaporanDesc()) {
            if (laporan.getStatus() != StatusLaporan.SELESAI
                    && cocokKataKunci(laporan, kataKunci) && lolosSemuaFilter(laporan, filter)) {
                hasil.add(laporan);
            }
        }
        return urutkanTerbaru(hasil);
    }

    /** jenis: "Kehilangan", "Temuan", atau kosong untuk semua jenis. */
    public List<Laporan> cariPerJenis(String jenis, String kataKunci, List<FilterLaporan> filter) {
        List<Laporan> hasil = cari(kataKunci, filter);
        if (jenis != null && !jenis.isBlank()) {
            hasil.removeIf(laporan -> !laporan.getJenisLaporan().equalsIgnoreCase(jenis));
        }
        return hasil;
    }

    public Laporan getLaporan(Long id) {
        return laporanRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laporan tidak ditemukan."));
    }

    // polymorphism: cocok() yang dijalankan bergantung pada jenis objek filternya
    private boolean lolosSemuaFilter(Laporan laporan, List<FilterLaporan> filter) {
        for (FilterLaporan f : filter) {
            if (!f.cocok(laporan)) {
                return false;
            }
        }
        return true;
    }

    private boolean cocokKataKunci(Laporan laporan, String kataKunci) {
        if (kataKunci == null || kataKunci.isBlank()) {
            return true;
        }
        String kunci = kataKunci.trim().toLowerCase(Locale.ROOT);
        return mengandung(laporan.getNamaBarang(), kunci) || mengandung(laporan.getDeskripsi(), kunci)
                || mengandung(laporan.getLokasi(), kunci);
    }

    private List<Laporan> urutkanTerbaru(List<Laporan> hasil) {
        hasil.sort(Comparator.comparing(Laporan::getTanggalKejadian).reversed()
                .thenComparing(Laporan::getIdLaporan, Comparator.reverseOrder()));
        return hasil;
    }

    private static boolean mengandung(String teks, String kunci) {
        return teks != null && teks.toLowerCase(Locale.ROOT).contains(kunci);
    }
}
