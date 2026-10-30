package com.balikinaja.service.storage;

import com.balikinaja.model.Foto;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Menyimpan foto ke folder lokal server (default: ./uploads). Dipakai saat dijalankan di laptop.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@Component
@ConditionalOnProperty(name = "balikinaja.penyimpanan-foto", havingValue = "lokal", matchIfMissing = true)
public class PenyimpananFotoLokal extends PenyimpananFotoDasar {

    private final Path folderUpload;

    public PenyimpananFotoLokal(@Value("${balikinaja.upload-dir:uploads}") String folderUpload) {
        this.folderUpload = Paths.get(folderUpload).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.folderUpload);
        } catch (IOException e) {
            throw new UncheckedIOException("Folder upload tidak dapat dibuat: " + this.folderUpload, e);
        }
    }

    @Override
    public Foto simpan(MultipartFile file) {
        if (!validasiFile(file)) {
            throw new IllegalArgumentException(PESAN_TIDAK_VALID);
        }
        String namaFile = buatNamaUnik(ekstensi(file.getOriginalFilename()));
        Path tujuan = folderUpload.resolve(namaFile);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, tujuan, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Foto gagal disimpan.", e);
        }
        return new Foto(namaFile, tujuan.toString(), file.getContentType(), file.getSize());
    }

    @Override
    public Foto simpan(byte[] isi, String namaAsli, String tipeFile) {
        String namaFile = buatNamaUnik(ekstensi(namaAsli));
        Path tujuan = folderUpload.resolve(namaFile);
        try {
            Files.write(tujuan, isi);
        } catch (IOException e) {
            throw new UncheckedIOException("Foto gagal disimpan.", e);
        }
        return new Foto(namaFile, tujuan.toString(), tipeFile, isi.length);
    }

    @Override
    public void hapus(Foto foto) {
        if (foto == null) {
            return;
        }
        try {
            Files.deleteIfExists(folderUpload.resolve(foto.getNamaFile()));
        } catch (IOException e) {
            // file yang gagal dihapus tidak menggagalkan proses utama
        }
    }

    public Path getFolderUpload() {
        return folderUpload;
    }
}
