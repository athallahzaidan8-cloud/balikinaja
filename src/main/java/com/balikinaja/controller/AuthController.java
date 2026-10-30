package com.balikinaja.controller;

import com.balikinaja.model.Pengguna;
import com.balikinaja.model.User;
import com.balikinaja.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Halaman registrasi, login, dan logout.
 * Fitur 1 - Login & Registrasi (PJ: Muhammad Zaidan Athallah)
 */
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String halamanLogin(@RequestParam(required = false) String next, HttpSession session, Model model) {
        User user = authService.getUserLogin(session);
        if (user != null) {
            return "redirect:" + user.getHalamanDashboard();
        }
        model.addAttribute("next", next);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        @RequestParam(required = false) String next, HttpSession session, Model model) {
        try {
            User user = authService.login(email, password);
            authService.simpanKeSession(user, session);
            // polymorphism: Pengguna -> /dashboard, Admin -> /admin
            return "redirect:" + (tujuanAman(next) ? next : user.getHalamanDashboard());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("next", next);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String halamanRegistrasi(HttpSession session) {
        User user = authService.getUserLogin(session);
        return user != null ? "redirect:" + user.getHalamanDashboard() : "auth/register";
    }

    @PostMapping("/register")
    public String registrasi(@RequestParam String nama, @RequestParam String email, @RequestParam String password,
                             @RequestParam(required = false) String noTelepon, @RequestParam String nomorIdentitas,
                             @RequestParam String jenisPengguna, HttpSession session, Model model,
                             RedirectAttributes redirect) {
        try {
            Pengguna baru = authService.registrasi(nama, email, password, noTelepon, nomorIdentitas, jenisPengguna);
            authService.simpanKeSession(baru, session);
            redirect.addFlashAttribute("sukses", "Akun berhasil dibuat. Selamat datang, " + baru.getNamaDepan() + "!");
            return "redirect:" + baru.getHalamanDashboard();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nama", nama);
            model.addAttribute("email", email);
            model.addAttribute("noTelepon", noTelepon);
            model.addAttribute("nomorIdentitas", nomorIdentitas);
            model.addAttribute("jenisPengguna", jenisPengguna);
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/";
    }

    /** Hanya path internal yang boleh dipakai sebagai tujuan setelah login. */
    private static boolean tujuanAman(String next) {
        return next != null && next.startsWith("/") && !next.startsWith("//") && !next.contains("\\");
    }
}
