package com.balikinaja.controller;

import com.balikinaja.model.Kategori;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.LaporanTemuanService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Form lapor barang temuan, edit, dan daftar barang temuan.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@Controller
public class LaporanTemuanController {

    private final AuthService authService;
    private final LaporanTemuanService laporanService;

    public LaporanTemuanController(AuthService authService, LaporanTemuanService laporanService) {
        this.authService = authService;
        this.laporanService = laporanService;
    }

    @InitBinder("laporan")
    void batasiKolom(WebDataBinder binder) {
        binder.setAllowedFields("namaBarang", "kategori", "deskripsi", "lokasi", "tanggalKejadian",
                "lokasiPenyimpanan", "pertanyaanVerifikasi", "jawabanVerifikasi");
    }

    @GetMapping("/temuan")
    public String daftarTemuan(Model model) {
        model.addAttribute("temuanList", laporanService.getTemuanTersedia());
        return "temuan";
    }

    @GetMapping("/laporan/temuan/baru")
    public String formBaru(Model model) {
        LaporanTemuan laporan = new LaporanTemuan();
        laporan.setTanggalKejadian(LocalDate.now());
        return tampilkanForm(model, laporan, null);
    }

    @PostMapping("/laporan/temuan")
    public String simpan(@ModelAttribute("laporan") LaporanTemuan data, @RequestParam("file") MultipartFile file,
                         HttpSession session, Model model, RedirectAttributes redirect) {
        Pengguna pelapor = authService.getPenggunaLogin(session);
        try {
            LaporanTemuan laporan = laporanService.buatLaporan(pelapor, data, file);
            redirect.addFlashAttribute("sukses", "Terima kasih! Laporan temuan berhasil dibuat.");
            return "redirect:/laporan/" + laporan.getIdLaporan();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return tampilkanForm(model, data, null);
        }
    }

    @GetMapping("/laporan/temuan/{id}/edit")
    public String formEdit(@PathVariable Long id, HttpSession session, Model model) {
        LaporanTemuan laporan = laporanService.getLaporanMilik(id, authService.getPenggunaLogin(session));
        return tampilkanForm(model, laporan, laporan);
    }

    @PostMapping("/laporan/temuan/{id}/edit")
    public String perbarui(@PathVariable Long id, @ModelAttribute("laporan") LaporanTemuan data,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           HttpSession session, Model model, RedirectAttributes redirect) {
        Pengguna pelapor = authService.getPenggunaLogin(session);
        try {
            laporanService.editLaporan(id, data, pelapor, file);
            redirect.addFlashAttribute("sukses", "Perubahan laporan disimpan.");
            return "redirect:/laporan/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return tampilkanForm(model, data, laporanService.getLaporanMilik(id, pelapor));
        }
    }

    private String tampilkanForm(Model model, LaporanTemuan laporan, LaporanTemuan asli) {
        model.addAttribute("laporan", laporan);
        model.addAttribute("asli", asli);
        model.addAttribute("kategoriList", Kategori.values());
        model.addAttribute("hariIni", LocalDate.now());
        return "laporan/form-temuan";
    }
}
