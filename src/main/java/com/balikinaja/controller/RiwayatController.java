package com.balikinaja.controller;

import com.balikinaja.model.StatusLaporan;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.RiwayatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Halaman "Laporan Saya", daftar semua laporan untuk Admin, dan tandai selesai.
 * Fitur 6 - Status & Riwayat Laporan (PJ: Putra)
 */
@Controller
public class RiwayatController {

    private final AuthService authService;
    private final RiwayatService riwayatService;

    public RiwayatController(AuthService authService, RiwayatService riwayatService) {
        this.authService = authService;
        this.riwayatService = riwayatService;
    }

    @GetMapping("/laporan/saya")
    public String laporanSaya(HttpSession session, Model model) {
        model.addAttribute("laporanList", riwayatService.getLaporanPengguna(authService.getPenggunaLogin(session)));
        return "laporan/saya";
    }

    @GetMapping("/admin/laporan")
    public String semuaLaporan(@RequestParam(required = false) StatusLaporan status, Model model) {
        model.addAttribute("laporanList", riwayatService.getSemuaLaporan(status));
        model.addAttribute("status", status);
        model.addAttribute("statusList", StatusLaporan.values());
        return "admin/laporan";
    }

    @PostMapping("/admin/laporan/{id}/selesai")
    public String tandaiSelesai(@PathVariable Long id, @RequestParam(required = false) String kembali,
                                HttpSession session, RedirectAttributes redirect) {
        try {
            riwayatService.tandaiSelesai(id, authService.getAdminLogin(session));
            redirect.addFlashAttribute("sukses", "Serah terima dicatat. Laporan ditandai selesai.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + ("detail".equals(kembali) ? "/laporan/" + id : "/admin");
    }
}
