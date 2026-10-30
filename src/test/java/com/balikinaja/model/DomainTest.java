package com.balikinaja.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.balikinaja.service.filter.FilterKategori;
import com.balikinaja.service.filter.FilterLaporan;
import com.balikinaja.service.filter.FilterLokasi;
import com.balikinaja.service.filter.FilterTanggal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/** Pengujian logika kelas tanpa database, dikelompokkan per fitur. */
class DomainTest {

    private final Pengguna rani = new Pengguna("Rani Pratiwi", "rani@kampus.id", BCrypt.hashpw("rahasia123", BCrypt.gensalt()),
            "0812", "103032400101", "Mahasiswa");
    private final Admin admin = new Admin("Admin", "admin@kampus.id", BCrypt.hashpw("admin12345", BCrypt.gensalt()),
            "0813", "Kemahasiswaan");
    private final Foto foto = new Foto("a.png", "/tmp/a.png", "image/png", 10);

    private LaporanTemuan temuan() {
        LaporanTemuan t = new LaporanTemuan("Kunci motor", Kategori.LAINNYA, "Kunci Honda", "Parkiran Selaru",
                LocalDate.now().minusDays(1), rani, "Pos Satpam", "Gantungannya berbentuk apa?", "Ikan");
        t.setFoto(foto);
        return t;
    }

    private LaporanKehilangan kehilangan() {
        LaporanKehilangan k = new LaporanKehilangan("Dompet", Kategori.AKSESORIS, "Dompet cokelat", "Gedung TULT",
                LocalDate.now().minusDays(2), rani, "Gantungan bintang");
        k.setFoto(foto);
        return k;
    }

    @Nested
    class Fitur1LoginRegistrasi {
        @Test
        void passwordDicekLewatHash() {
            assertTrue(rani.cekPassword("rahasia123"));
            assertFalse(rani.cekPassword("salah"));
        }

        @Test
        void dashboardBerbedaSesuaiPeran() {
            List<User> akun = List.of(rani, admin);
            assertEquals("/dashboard", akun.get(0).getHalamanDashboard());
            assertEquals("/admin", akun.get(1).getHalamanDashboard());
        }
    }

    @Nested
    class Fitur2LaporBarangHilang {
        @Test
        void statusAwalDicari() {
            assertEquals(StatusLaporan.DICARI, kehilangan().getStatusAwal());
        }

        @Test
        void fotoWajibAda() {
            LaporanKehilangan k = kehilangan();
            assertTrue(k.validasi());
            k.setFoto(null);
            assertFalse(k.validasi());
        }

        @Test
        void tanggalTidakBolehDiMasaDepan() {
            LaporanKehilangan k = kehilangan();
            k.setTanggalKejadian(LocalDate.now().plusDays(1));
            assertFalse(k.validasi());
        }
    }

    @Nested
    class Fitur3LaporBarangTemuan {
        @Test
        void statusAwalDitemukanDanWajibPertanyaan() {
            LaporanTemuan t = temuan();
            assertEquals(StatusLaporan.DITEMUKAN, t.getStatusAwal());
            assertTrue(t.validasi());
            t.setPertanyaanVerifikasi(" ");
            assertFalse(t.validasi());
        }

        @Test
        void editTanpaJawabanMempertahankanJawabanLama() {
            LaporanTemuan t = temuan();
            LaporanTemuan form = temuan();
            form.setJawabanVerifikasi("");
            t.perbaruiDari(form);
            assertTrue(t.periksaJawaban("ikan"));
        }
    }

    @Nested
    class Fitur4CariFilter {
        @Test
        void setiapFilterMenilaiLaporan() {
            LaporanTemuan t = temuan();
            List<FilterLaporan> lolos = List.of(new FilterKategori(Kategori.LAINNYA), new FilterLokasi("selaru"),
                    new FilterTanggal(LocalDate.now().minusDays(7), LocalDate.now()));
            assertTrue(lolos.stream().allMatch(f -> f.cocok(t)));
            assertFalse(new FilterKategori(Kategori.ELEKTRONIK).cocok(t));
            assertFalse(new FilterLokasi("TULT").cocok(t));
            assertFalse(new FilterTanggal(LocalDate.now(), null).cocok(t));
        }
    }

    @Nested
    class Fitur5KlaimVerifikasi {
        @Test
        void jawabanDicocokkanTanpaPekaHurufDanSpasi() {
            LaporanTemuan t = temuan();
            assertTrue(t.periksaJawaban("  IKAN "));
            assertFalse(t.periksaJawaban("bintang"));
        }

        @Test
        void klaimHanyaBisaDiputuskanSekali() {
            Klaim klaim = new Klaim(rani, temuan(), "ikan");
            assertEquals(StatusKlaim.MENUNGGU, klaim.getStatus());
            klaim.setujui(admin, "Ambil di pos");
            assertEquals(StatusKlaim.DISETUJUI, klaim.getStatus());
            assertThrows(IllegalStateException.class, () -> klaim.tolak(admin, "telat"));
        }

        @Test
        void temuanTidakBisaDiklaimSetelahDiklaim() {
            LaporanTemuan t = temuan();
            t.ubahStatus(StatusLaporan.DITEMUKAN, rani, "dibuat");
            assertTrue(t.bisaDiklaim());
            t.ubahStatus(StatusLaporan.DIKLAIM, admin, "disetujui");
            assertFalse(t.bisaDiklaim());
        }
    }

    @Nested
    class Fitur6StatusRiwayat {
        @Test
        void aturanPerpindahanStatus() {
            assertTrue(StatusLaporan.DICARI.bisaBerubahKe(StatusLaporan.DITEMUKAN));
            assertTrue(StatusLaporan.DITEMUKAN.bisaBerubahKe(StatusLaporan.DICARI));
            assertFalse(StatusLaporan.DICARI.bisaBerubahKe(StatusLaporan.SELESAI));
            assertFalse(StatusLaporan.SELESAI.bisaBerubahKe(StatusLaporan.DICARI));
        }

        @Test
        void setiapPerubahanTercatatDiRiwayat() {
            LaporanKehilangan k = kehilangan();
            k.ubahStatus(StatusLaporan.DICARI, rani, "Laporan dibuat");
            k.ubahStatus(StatusLaporan.DITEMUKAN, rani, "Klaim diajukan");
            assertEquals(2, k.getRiwayat().size());
            assertNull(k.getRiwayat().get(0).getStatusLama());
            assertEquals(StatusLaporan.DICARI, k.getRiwayat().get(1).getStatusLama());
            assertThrows(IllegalStateException.class, () -> k.ubahStatus(StatusLaporan.SELESAI, admin, "lompat"));
        }
    }
}
