package com.balikinaja.config;

import com.balikinaja.model.Admin;
import com.balikinaja.model.Kategori;
import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.repository.LaporanRepository;
import com.balikinaja.repository.UserRepository;
import com.balikinaja.service.AuthService;
import com.balikinaja.service.storage.PenyimpananFoto;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Mengisi akun dan laporan contoh saat database masih kosong, supaya aplikasi
 * langsung bisa dicoba. Matikan dengan balikinaja.seed-demo=false.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepo;
    private final LaporanRepository laporanRepo;
    private final AuthService authService;
    private final PenyimpananFoto penyimpananFoto;
    private final boolean aktif;
    private final String passwordAdmin;
    private final String passwordDemo;

    /**
     * Saat online, password akun contoh diambil dari environment variable ADMIN_PASSWORD dan
     * DEMO_PASSWORD supaya tidak bisa ditebak orang lain. Di laptop memakai password bawaan.
     */
    public DataSeeder(UserRepository userRepo, LaporanRepository laporanRepo, AuthService authService,
                      PenyimpananFoto penyimpananFoto, @Value("${balikinaja.seed-demo:true}") boolean aktif,
                      @Value("${balikinaja.admin-password:}") String passwordAdmin,
                      @Value("${balikinaja.demo-password:}") String passwordDemo) {
        this.userRepo = userRepo;
        this.laporanRepo = laporanRepo;
        this.authService = authService;
        this.penyimpananFoto = penyimpananFoto;
        this.aktif = aktif;
        this.passwordAdmin = passwordAdmin;
        this.passwordDemo = passwordDemo;
    }

    private String password(String khusus, String bawaan) {
        return khusus == null || khusus.isBlank() ? bawaan : khusus;
    }

    @Override
    public void run(String... args) {
        if (!aktif || userRepo.count() > 0) {
            return;
        }
        userRepo.save(new Admin("Admin BalikinAja", "admin@balikinaja.id", authService.buatHash(password(passwordAdmin, "admin12345")),
                "081200000001", "Direktorat Kemahasiswaan"));
        Pengguna rani = userRepo.save(new Pengguna("Rani Pratiwi", "rani@student.telkomuniversity.ac.id",
                authService.buatHash(password(passwordDemo, "rani12345")), "081200000002", "103032400101", "Mahasiswa"));
        Pengguna dimas = userRepo.save(new Pengguna("Dimas Saputra", "dimas@student.telkomuniversity.ac.id",
                authService.buatHash(password(passwordDemo, "dimas12345")), "081200000003", "103032400102", "Mahasiswa"));
        LocalDate hariIni = LocalDate.now();

        simpan(new LaporanKehilangan("Dompet kulit cokelat", Kategori.AKSESORIS,
                "Dompet lipat warna cokelat tua, berisi KTM dan beberapa kartu e-money.",
                "Gedung TULT lantai 3", hariIni.minusDays(1), rani,
                "Ada gantungan kecil berbentuk bintang di resletingnya"), rani, new Color(0x6B4A2E));
        simpan(new LaporanTemuan("Kunci motor dengan gantungan biru", Kategori.LAINNYA,
                "Kunci motor Honda, gantungannya karet berwarna biru.",
                "Parkiran Gedung Selaru", hariIni.minusDays(2), rani, "Pos Satpam Gerbang Utama",
                "Gantungan kuncinya berbentuk apa?", "ikan"), rani, new Color(0x1F3954));
        simpan(new LaporanTemuan("Botol minum hitam 750 ml", Kategori.LAINNYA,
                "Botol minum stainless warna hitam, ada stiker di bagian bawah.",
                "Kantin Asrama Putra", hariIni.minusDays(3), dimas, "Ruang Direktorat Kemahasiswaan",
                "Stiker apa yang ada di bawah botol?", "kucing"), dimas, new Color(0x2E241F));
        simpan(new LaporanTemuan("Charger laptop 65W", Kategori.ELEKTRONIK,
                "Charger laptop USB-C warna putih, kabel sedikit terkelupas.",
                "Perpustakaan Open Library lantai 2", hariIni.minusDays(1), dimas, "Meja sirkulasi Open Library",
                "Merek charger apa?", "lenovo"), dimas, new Color(0x2E7D5B));
        simpan(new LaporanKehilangan("Jaket hoodie abu-abu", Kategori.PAKAIAN,
                "Hoodie abu-abu ukuran L dengan tulisan himpunan di punggung.",
                "Masjid Syamsul Ulum", hariIni.minusDays(4), dimas,
                "Ada nama DIMAS dijahit di label dalam"), dimas, new Color(0x66574F));

        if (passwordAdmin.isBlank() && passwordDemo.isBlank()) {
            log.info("Data contoh dibuat. Admin: admin@balikinaja.id / admin12345, "
                    + "Pengguna: rani@student.telkomuniversity.ac.id / rani12345, dimas@student.telkomuniversity.ac.id / dimas12345");
        } else {
            log.info("Data contoh dibuat. Password akun contoh memakai ADMIN_PASSWORD dan DEMO_PASSWORD.");
        }
    }

    private void simpan(Laporan laporan, Pengguna pelapor, Color warna) {
        laporan.setFoto(penyimpananFoto.simpan(buatGambar(laporan.getNamaBarang(), laporan.getKategori(), warna),
                "contoh.png", "image/png"));
        laporan.ubahStatus(laporan.getStatusAwal(), pelapor, "Laporan " + laporan.getJenisLaporan().toLowerCase() + " dibuat");
        laporanRepo.save(laporan);
    }

    /** Gambar ilustrasi sederhana sebagai pengganti foto asli pada data contoh. */
    private static byte[] buatGambar(String judul, Kategori kategori, Color warna) {
        int w = 800, h = 600;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, warna, w, h, warna.darker()));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(255, 255, 255, 40));
        g.setStroke(new BasicStroke(3f));
        g.drawOval(w - 360, -160, 560, 560);
        g.drawOval(-220, h - 260, 520, 520);
        // label kategori di bawah, supaya tidak tertutup badge status pada kartu
        g.setColor(new Color(0x33CC7A));
        g.fillRoundRect(56, h - 260, 250, 52, 52, 52);
        g.setColor(new Color(0x1B1411));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g.drawString(kategori.getLabel(), 80, h - 225);
        g.setColor(new Color(0xFAF5F0));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));
        int y = h - 160;
        for (String baris : bungkus(judul, 26)) {
            g.drawString(baris, 56, y);
            y += 56;
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        g.setColor(new Color(250, 245, 240, 170));
        g.drawString("Foto contoh - BalikinAja", 56, h - 40);
        g.dispose();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> bungkus(String teks, int lebar) {
        List<String> baris = new ArrayList<>();
        StringBuilder sekarang = new StringBuilder();
        for (String kata : teks.split(" ")) {
            if (sekarang.length() + kata.length() + 1 > lebar && !sekarang.isEmpty()) {
                baris.add(sekarang.toString());
                sekarang.setLength(0);
            }
            sekarang.append(sekarang.isEmpty() ? "" : " ").append(kata);
        }
        baris.add(sekarang.toString());
        return baris;
    }
}
