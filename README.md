# BalikinAja

Sistem informasi barang hilang dan temuan di lingkungan kampus, berbasis web.
Tugas Besar Pemrograman Berorientasi Objek, Kelompok 5 IT-48-03, Universitas Telkom.

Java 17+, Spring Boot 3.5, Thymeleaf, Spring Data JPA, MySQL.

## Menjalankan

1. Nyalakan MySQL XAMPP (XAMPP Control Panel → Start pada MySQL).
   Konfigurasi default memakai **port 3307**, user `root`, tanpa password.
   - Jika MySQL XAMPP-mu masih di port 3306 dan tidak bentrok dengan MySQL lain, ganti `3307` menjadi `3306`
     di `src/main/resources/application.properties`.
   - Jika memakai MySQL lain yang ber-password, isi `spring.datasource.password` (atau environment variable `DB_PASSWORD`).
2. Jalankan:
   - **NetBeans**: *Open Project*, pilih folder `balikinaja`, lalu klik *Run* (F6). Tunggu sampai log menampilkan
     `BalikinAja berjalan`, browser akan terbuka otomatis ke http://localhost:8080.
   - **Terminal**: `mvn spring-boot:run`
3. Untuk menghentikan aplikasi, klik tombol kotak merah (Stop) di panel Output NetBeans.

Database `balikinaja` dan tabel-tabelnya dibuat otomatis. Saat database masih kosong, aplikasi mengisi data contoh:

| Peran | Email | Password |
|---|---|---|
| Admin | admin@balikinaja.id | admin12345 |
| Pengguna | rani@student.telkomuniversity.ac.id | rani12345 |
| Pengguna | dimas@student.telkomuniversity.ac.id | dimas12345 |

Matikan data contoh dengan `balikinaja.seed-demo=false`. Foto yang diunggah disimpan di folder `uploads/`.

Uji logika kelas: `mvn test` (13 pengujian di `DomainTest`, dikelompokkan per fitur).

## Versi online (gratis): Render + Aiven

- **Database**: Aiven for MySQL paket *Free*. Dari halaman layanan, ambil Host, Port, User, Password.
- **Aplikasi**: Render, *New + → Blueprint*, pilih repo ini. `render.yaml` dan `Dockerfile` sudah menyiapkan semuanya. Isi:
  - `DB_URL` = `jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED`
  - `DB_USERNAME`, `DB_PASSWORD` dari Aiven
- `ADMIN_PASSWORD` dan `DEMO_PASSWORD` dibuat acak oleh Render (lihat tab *Environment*), dipakai untuk
  `admin@balikinaja.id`, `rani@student.telkomuniversity.ac.id`, dan `dimas@student.telkomuniversity.ac.id`.
- Saat online foto disimpan di database (`PenyimpananFotoDatabase`, dikompres maks. ~450 KB) karena disk Render gratis
  terhapus setiap restart. Di laptop foto tetap di folder `uploads/` (`PenyimpananFotoLokal`).
- Paket gratis Render "tidur" setelah 15 menit tanpa pengunjung; pembukaan pertama sesudahnya butuh sekitar 1 menit.
- Google: daftarkan situs di Google Search Console (metode *HTML tag*), isi kodenya ke environment variable
  `GOOGLE_SITE_VERIFICATION`, lalu kirim `https://<situs>/sitemap.xml`.

## Pembagian file per fitur

Setiap file punya komentar `Fitur n - ... (PJ: ...)` di bagian atasnya.

| Fitur | PJ | Model | Service | Controller & halaman |
|---|---|---|---|---|
| F-01 Login & Registrasi | Muhammad Zaidan Athallah | `User` (abstract), `Pengguna`, `Admin` | `AuthService`, `AksesInterceptor` | `AuthController`, `auth/login.html`, `auth/register.html` |
| F-02 Lapor Barang Hilang | Raja Pandya Danendra Algesta | `Laporan` (abstract), `LaporanKehilangan`, `Kategori` | `LaporanKehilanganService` | `LaporanKehilanganController`, `laporan/form-hilang.html` |
| F-03 Lapor Barang Temuan | Keysha Dhealubna Ernaldy | `LaporanTemuan`, `Foto` | `LaporanTemuanService`, `PenyimpananFoto` (interface), `PenyimpananFotoLokal` | `LaporanTemuanController`, `laporan/form-temuan.html`, `temuan.html` |
| F-04 Cari & Filter Barang | Zazkia Maharrani Adzulvi | — | `PencarianService`, `FilterLaporan` (interface), `FilterKategori`, `FilterLokasi`, `FilterTanggal` | `PencarianController`, `cari.html`, `laporan/detail.html` |
| F-05 Klaim & Verifikasi Admin | Atika Supri Nandya | `Klaimable` (interface), `Klaim`, `StatusKlaim` | `KlaimService` | `KlaimController`, `klaim/saya.html`, `admin/klaim-*.html` |
| F-06 Status & Riwayat Laporan | Putra | `StatusLaporan`, `RiwayatStatus`, `Laporan.ubahStatus()` | `RiwayatService` | `RiwayatController`, `laporan/saya.html`, `admin/laporan.html` |

Dikerjakan bersama: `HomeController` (beranda, dashboard), `AdminController`, `DataSeeder`, `WebConfig`, tampilan (`fragments/`, `css/balikinaja.css`).

## Konsep PBO di kode

- **Inheritance**: `Pengguna`/`Admin` extends `User`; `LaporanKehilangan`/`LaporanTemuan` extends `Laporan`.
- **Abstract class**: `User` (`getRole()`, `getHalamanDashboard()`), `Laporan` (`getJenisLaporan()`, `getStatusAwal()`, `validasi()`).
- **Interface**: `Klaimable`, `PenyimpananFoto`, `FilterLaporan`.
- **Encapsulation**: `password` hanya dicek lewat `cekPassword()`; `jawabanVerifikasi` tanpa getter, hanya lewat `periksaJawaban()`; status hanya berubah lewat `ubahStatus()`.
- **Polymorphism**: redirect setelah login memakai `user.getHalamanDashboard()`; pencarian menjalankan `List<FilterLaporan>` tanpa tahu jenis filternya.

## Kredit

Tampilan diadaptasi dari *App landing page template* oleh [8B](https://8b.com/) (lisensi MIT). Font Host Grotesk dari Google Fonts.
