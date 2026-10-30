package com.balikinaja.controller;

import com.balikinaja.model.Kategori;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.LaporanKehilanganService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Form lapor barang hilang, edit, dan hapus.
 * Fitur 2 - Lapor Barang Hilang (PJ: Raja Pandya Danendra Algesta)
 */
@Controller
@RequestMapping("/laporan/hilang")
public class LaporanKehilanganController {

    private final AuthService authService;
    private final LaporanKehilanganService laporanService;

    public LaporanKehilanganController(AuthService authService, LaporanKehilanganService laporanService) {
        this.authService = authService;
        this.laporanService = laporanService;
    }

    /** Hanya kolom form yang boleh diisi dari request (status, pelapor, dll. tidak bisa disusupkan). */
    @InitBinder("laporan")
    void batasiKolom(WebDataBinder binder) {
        binder.setAllowedFields("namaBarang", "kategori", "deskripsi", "lokasi", "tanggalKejadian",
                "ciriKhusus", "kontakAlternatif");
    }

    @GetMapping("/baru")
    public String formBaru(Model model) {
        LaporanKehilangan laporan = new LaporanKehilangan();
        laporan.setTanggalKejadian(LocalDate.now());
        return tampilkanForm(model, laporan, null);
    }

    @PostMapping
    public String simpan(@ModelAttribute("laporan") LaporanKehilangan data, @RequestParam("file") MultipartFile file,
                         HttpSession session, Model model, RedirectAttributes redirect) {
        Pengguna pelapor = authService.getPenggunaLogin(session);
        try {
            LaporanKehilangan laporan = laporanService.buatLaporan(pelapor, data, file);
            redirect.addFlashAttribute("sukses", "Laporan kehilangan berhasil dibuat. Semoga barangmu cepat kembali!");
            return "redirect:/laporan/" + laporan.getIdLaporan();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return tampilkanForm(model, data, null);
        }
    }

    @GetMapping("/{id}/edit")
    public String formEdit(@PathVariable Long id, HttpSession session, Model model) {
        LaporanKehilangan laporan = laporanService.getLaporanMilik(id, authService.getPenggunaLogin(session));
        return tampilkanForm(model, laporan, laporan);
    }

    @PostMapping("/{id}/edit")
    public String perbarui(@PathVariable Long id, @ModelAttribute("laporan") LaporanKehilangan data,
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

    @PostMapping("/{id}/hapus")
    public String hapus(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        try {
            laporanService.hapusLaporan(id, authService.getPenggunaLogin(session));
            redirect.addFlashAttribute("sukses", "Laporan kehilangan dihapus.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/laporan/saya";
    }

    /** asli: laporan yang sedang diedit (null untuk laporan baru). */
    private String tampilkanForm(Model model, LaporanKehilangan laporan, LaporanKehilangan asli) {
        model.addAttribute("laporan", laporan);
        model.addAttribute("asli", asli);
        model.addAttribute("kategoriList", Kategori.values());
        model.addAttribute("hariIni", LocalDate.now());
        return "laporan/form-hilang";
    }
}
