package com.balikinaja.controller;

import com.balikinaja.service.storage.PenyimpananFotoDatabase;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Menyajikan foto yang disimpan di database (mode online) lewat URL /uploads/nama-file.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@RestController
@ConditionalOnProperty(name = "balikinaja.penyimpanan-foto", havingValue = "database")
public class FotoController {

    private final PenyimpananFotoDatabase penyimpananFoto;

    public FotoController(PenyimpananFotoDatabase penyimpananFoto) {
        this.penyimpananFoto = penyimpananFoto;
    }

    @GetMapping("/uploads/{namaFile:.+}")
    public ResponseEntity<byte[]> foto(@PathVariable String namaFile) {
        // nama file unik dan tidak pernah berubah, jadi aman di-cache browser lama
        return penyimpananFoto.muat(namaFile)
                .map(isi -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(isi.getTipeFile()))
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                        .body(isi.getIsi()))
                .orElse(ResponseEntity.notFound().build());
    }
}
