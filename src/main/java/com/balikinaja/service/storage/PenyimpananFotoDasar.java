package com.balikinaja.service.storage;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * Aturan yang sama untuk semua cara penyimpanan foto: format, ukuran, dan nama file unik.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
public abstract class PenyimpananFotoDasar implements PenyimpananFoto {

    public static final long MAKS_UKURAN = 10L * 1024 * 1024; // 10 MB
    public static final Set<String> TIPE_DIIZINKAN = Set.of("jpg", "jpeg", "png");
    protected static final String PESAN_TIDAK_VALID = "Foto wajib berformat JPG/JPEG/PNG dengan ukuran maksimal 10 MB.";

    @Override
    public boolean validasiFile(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MAKS_UKURAN) {
            return false;
        }
        String tipe = file.getContentType();
        return TIPE_DIIZINKAN.contains(ekstensi(file.getOriginalFilename()))
                && tipe != null && tipe.startsWith("image/");
    }

    protected String buatNamaUnik(String ekstensi) {
        return UUID.randomUUID() + "." + ekstensi;
    }

    protected static String ekstensi(String namaFile) {
        if (namaFile == null || !namaFile.contains(".")) {
            return "";
        }
        return namaFile.substring(namaFile.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
