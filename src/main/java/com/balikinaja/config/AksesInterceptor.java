package com.balikinaja.config;

import com.balikinaja.model.Admin;
import com.balikinaja.model.User;
import com.balikinaja.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Membatasi halaman: /admin/** hanya untuk Admin, halaman pengguna hanya untuk Pengguna.
 * Fitur 1 - Login & Registrasi (PJ: Muhammad Zaidan Athallah)
 */
@Component
public class AksesInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AksesInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        User user = authService.getUserLogin(request.getSession());
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (user == null) {
            String tujuan = URLEncoder.encode(path, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/login?next=" + tujuan);
            return false;
        }
        boolean halamanAdmin = path.startsWith("/admin");
        if (halamanAdmin != (user instanceof Admin)) {
            // Admin dan Pengguna masing-masing diarahkan ke dashboard-nya sendiri
            response.sendRedirect(request.getContextPath() + user.getHalamanDashboard());
            return false;
        }
        return true;
    }
}
