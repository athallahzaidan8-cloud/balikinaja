package com.balikinaja.config;

import com.balikinaja.model.User;
import com.balikinaja.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Data yang dipakai semua halaman (akun login) dan penanganan error umum. */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final AuthService authService;
    private final boolean tampilkanAkunDemo;
    private final String googleVerification;

    public GlobalControllerAdvice(AuthService authService,
                                  @Value("${balikinaja.tampilkan-akun-demo:true}") boolean tampilkanAkunDemo,
                                  @Value("${balikinaja.google-site-verification:}") String googleVerification) {
        this.authService = authService;
        this.tampilkanAkunDemo = tampilkanAkunDemo;
        this.googleVerification = googleVerification;
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpServletRequest request, HttpSession session) {
        // permintaan foto tidak butuh data akun, hemat query ke database
        return request.getRequestURI().startsWith("/uploads/") ? null : authService.getUserLogin(session);
    }

    /** Kotak akun demo di halaman login; dimatikan saat aplikasi online. */
    @ModelAttribute("tampilkanAkunDemo")
    public boolean tampilkanAkunDemo() {
        return tampilkanAkunDemo;
    }

    /** Kode verifikasi Google Search Console (meta tag), kosong bila belum dipakai. */
    @ModelAttribute("googleVerification")
    public String googleVerification() {
        return googleVerification;
    }

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /** Misalnya id laporan di URL tidak ada atau bukan milik pengguna. */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String dataTidakValid(RuntimeException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("pesan", e.getMessage());
        return "error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileTerlaluBesar(HttpServletRequest request, RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", "Ukuran foto terlalu besar. Maksimal 10 MB.");
        String asal = request.getHeader("Referer");
        return "redirect:" + (asal != null ? asal : "/");
    }
}
