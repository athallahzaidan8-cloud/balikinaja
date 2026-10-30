package com.balikinaja.controller;

import com.balikinaja.model.Kategori;
import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.User;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.KlaimService;
import com.balikinaja.service.PencarianService;
import com.balikinaja.service.filter.FilterKategori;
import com.balikinaja.service.filter.FilterLaporan;
import com.balikinaja.service.filter.FilterLokasi;
import com.balikinaja.service.filter.FilterTanggal;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Halaman cari barang dan detail laporan.
 * Fitur 4 - Cari & Filter Barang (PJ: Zazkia Maharrani Adzulvi)
 */
@Controller
public class PencarianController {

    private final PencarianService pencarianService;
    private final KlaimService klaimService;
    private final AuthService authService;

    public PencarianController(PencarianService pencarianService, KlaimService klaimService, AuthService authService) {
        this.pencarianService = pencarianService;
        this.klaimService = klaimService;
        this.authService = authService;
    }

    @GetMapping("/cari")
    public String cari(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String jenis,
                       @RequestParam(required = false) Kategori kategori,
                       @RequestParam(required = false) String lokasi,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dari,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sampai,
                       Model model) {
        List<FilterLaporan> filter = new ArrayList<>();
        if (kategori != null) {
            filter.add(new FilterKategori(kategori));
        }
        if (lokasi != null && !lokasi.isBlank()) {
            filter.add(new FilterLokasi(lokasi));
        }
        if (dari != null || sampai != null) {
            filter.add(new FilterTanggal(dari, sampai));
        }
        model.addAttribute("hasil", pencarianService.cariPerJenis(jenis, q, filter));
        model.addAttribute("q", q);
        model.addAttribute("jenis", jenis);
        model.addAttribute("kategori", kategori);
        model.addAttribute("lokasi", lokasi);
        model.addAttribute("dari", dari);
        model.addAttribute("sampai", sampai);
        model.addAttribute("kategoriList", Kategori.values());
        model.addAttribute("adaFilter", !filter.isEmpty() || (q != null && !q.isBlank())
                || (jenis != null && !jenis.isBlank()));
        return "cari";
    }

    @GetMapping("/laporan/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Laporan laporan = pencarianService.getLaporan(id);
        User user = authService.getUserLogin(session);
        model.addAttribute("laporan", laporan);
        model.addAttribute("milikSaya", laporan.milik(user));
        if (laporan instanceof LaporanTemuan temuan) {
            Pengguna pengguna = authService.getPenggunaLogin(session);
            boolean bisaKlaim = temuan.bisaDiklaim() && !temuan.milik(user);
            model.addAttribute("temuan", temuan);
            model.addAttribute("bisaKlaim", bisaKlaim);
            if (pengguna != null && bisaKlaim) {
                model.addAttribute("kehilanganSaya", klaimService.getKehilanganBisaDitautkan(pengguna));
            }
            if (user != null && (laporan.milik(user) || authService.getAdminLogin(session) != null)) {
                model.addAttribute("klaimList", klaimService.getKlaimUntukTemuan(temuan));
            }
        }
        return "laporan/detail";
    }
}
