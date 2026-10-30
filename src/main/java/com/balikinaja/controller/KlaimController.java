package com.balikinaja.controller;

import com.balikinaja.model.Klaim;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.KlaimService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pengajuan klaim oleh Pengguna dan halaman verifikasi klaim untuk Admin.
 * Fitur 5 - Klaim Barang & Verifikasi Admin (PJ: Atika Supri Nandya)
 */
@Controller
public class KlaimController {

    private final AuthService authService;
    private final KlaimService klaimService;

    public KlaimController(AuthService authService, KlaimService klaimService) {
        this.authService = authService;
        this.klaimService = klaimService;
    }

    @PostMapping("/klaim")
    public String ajukan(@RequestParam Long idTemuan, @RequestParam String jawaban,
                         @RequestParam(required = false) Long idKehilangan,
                         HttpSession session, RedirectAttributes redirect) {
        try {
            klaimService.ajukanKlaim(authService.getPenggunaLogin(session), idTemuan, jawaban, idKehilangan);
            redirect.addFlashAttribute("sukses", "Klaim terkirim. Admin akan memverifikasi jawabanmu.");
            return "redirect:/klaim/saya";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/laporan/" + idTemuan;
        }
    }

    @GetMapping("/klaim/saya")
    public String klaimSaya(HttpSession session, Model model) {
        model.addAttribute("klaimList", klaimService.getKlaimSaya(authService.getPenggunaLogin(session)));
        return "klaim/saya";
    }

    @GetMapping("/admin/klaim")
    public String daftarMenunggu(Model model) {
        model.addAttribute("klaimList", klaimService.getKlaimMenunggu());
        return "admin/klaim-list";
    }

    @GetMapping("/admin/klaim/{id}")
    public String detailVerifikasi(@PathVariable Long id, Model model) {
        Klaim klaim = klaimService.getKlaim(id);
        model.addAttribute("klaim", klaim);
        model.addAttribute("klaimLain", klaimService.getKlaimUntukTemuan(klaim.getLaporanTemuan()).stream()
                .filter(k -> !k.getIdKlaim().equals(id)).toList());
        return "admin/klaim-detail";
    }

    @PostMapping("/admin/klaim/{id}/verifikasi")
    public String verifikasi(@PathVariable Long id, @RequestParam String keputusan,
                             @RequestParam(required = false) String catatan,
                             HttpSession session, RedirectAttributes redirect) {
        try {
            boolean disetujui = "setujui".equals(keputusan);
            klaimService.verifikasi(id, authService.getAdminLogin(session), disetujui, catatan);
            redirect.addFlashAttribute("sukses", disetujui
                    ? "Klaim disetujui. Barang menunggu diambil pemiliknya."
                    : "Klaim ditolak.");
            return "redirect:/admin/klaim";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/klaim/" + id;
        }
    }
}
