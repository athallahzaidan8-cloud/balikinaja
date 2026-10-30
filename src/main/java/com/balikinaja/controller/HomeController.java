package com.balikinaja.controller;

import com.balikinaja.model.Laporan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.StatusLaporan;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.KlaimService;
import com.balikinaja.service.LaporanTemuanService;
import com.balikinaja.service.RiwayatService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Landing page dan dashboard Pengguna (dikerjakan bersama). */
@Controller
public class HomeController {

    private final AuthService authService;
    private final LaporanTemuanService laporanTemuanService;
    private final RiwayatService riwayatService;
    private final KlaimService klaimService;

    public HomeController(AuthService authService, LaporanTemuanService laporanTemuanService,
                          RiwayatService riwayatService, KlaimService klaimService) {
        this.authService = authService;
        this.laporanTemuanService = laporanTemuanService;
        this.riwayatService = riwayatService;
        this.klaimService = klaimService;
    }

    @GetMapping("/")
    public String beranda(Model model) {
        var temuan = laporanTemuanService.getTemuanTersedia();
        model.addAttribute("temuanTerbaru", temuan.stream().limit(6).toList());
        model.addAttribute("jumlahTemuan", temuan.size());
        model.addAttribute("jumlahDicari", riwayatService.hitungLaporan(StatusLaporan.DICARI));
        model.addAttribute("jumlahSelesai", riwayatService.hitungLaporan(StatusLaporan.SELESAI));
        return "index";
    }

    /** Pilihan jenis laporan: kehilangan atau temuan. */
    @GetMapping("/lapor")
    public String pilihLaporan() {
        return "lapor";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Pengguna pengguna = authService.getPenggunaLogin(session);
        List<Laporan> laporan = riwayatService.getLaporanPengguna(pengguna);
        model.addAttribute("laporanSaya", laporan.stream().limit(4).toList());
        model.addAttribute("jumlahLaporan", laporan.size());
        model.addAttribute("jumlahAktif", laporan.stream().filter(l -> l.getStatus() != StatusLaporan.SELESAI).count());
        model.addAttribute("klaimSaya", klaimService.getKlaimSaya(pengguna).stream().limit(4).toList());
        model.addAttribute("temuanTerbaru", laporanTemuanService.getTemuanTersedia().stream()
                .filter(t -> !t.milik(pengguna)).limit(3).toList());
        return "dashboard";
    }
}
