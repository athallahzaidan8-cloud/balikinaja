package com.balikinaja.service;

import com.balikinaja.model.Admin;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.User;
import com.balikinaja.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Registrasi, login, dan logout berbasis session.
 * Fitur 1 - Login & Registrasi (PJ: Muhammad Zaidan Athallah)
 */
@Service
public class AuthService {

    public static final String SESSION_USER_ID = "userId";
    private static final Pattern FORMAT_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int PANJANG_MIN_PASSWORD = 8;

    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Pengguna registrasi(String nama, String email, String password, String noTelepon,
                               String nomorIdentitas, String jenisPengguna) {
        if (kosong(nama) || kosong(email) || kosong(password) || kosong(nomorIdentitas) || kosong(jenisPengguna)) {
            throw new IllegalArgumentException("Semua kolom bertanda * wajib diisi.");
        }
        email = email.trim().toLowerCase();
        if (!validasiEmail(email)) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email sudah terdaftar. Silakan masuk.");
        }
        if (password.length() < PANJANG_MIN_PASSWORD) {
            throw new IllegalArgumentException("Password minimal " + PANJANG_MIN_PASSWORD + " karakter.");
        }
        Pengguna baru = new Pengguna(nama.trim(), email, hashPassword(password), noTelepon,
                nomorIdentitas.trim(), jenisPengguna);
        return userRepo.save(baru);
    }

    public User login(String email, String password) {
        User user = kosong(email) ? null : userRepo.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (user == null || !user.cekPassword(password)) {
            throw new IllegalArgumentException("Email atau password salah.");
        }
        return user;
    }

    /** Menyimpan id akun ke session setelah login berhasil. */
    public void simpanKeSession(User user, HttpSession session) {
        session.setAttribute(SESSION_USER_ID, user.getIdUser());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    /** Akun yang sedang login, atau null bila belum login. */
    public User getUserLogin(HttpSession session) {
        Object id = session.getAttribute(SESSION_USER_ID);
        return id instanceof Long idUser ? userRepo.findById(idUser).orElse(null) : null;
    }

    /** Pengguna yang sedang login, atau null bila belum login / yang login adalah Admin. */
    public Pengguna getPenggunaLogin(HttpSession session) {
        return getUserLogin(session) instanceof Pengguna pengguna ? pengguna : null;
    }

    public Admin getAdminLogin(HttpSession session) {
        return getUserLogin(session) instanceof Admin admin ? admin : null;
    }

    /** Dipakai DataSeeder untuk membuat akun contoh dengan password yang sudah di-hash. */
    public String buatHash(String password) {
        return hashPassword(password);
    }

    private boolean validasiEmail(String email) {
        return FORMAT_EMAIL.matcher(email).matches();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean kosong(String teks) {
        return teks == null || teks.isBlank();
    }
}
