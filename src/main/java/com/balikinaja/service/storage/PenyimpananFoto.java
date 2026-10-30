package com.balikinaja.service.storage;

import com.balikinaja.model.Foto;
import org.springframework.web.multipart.MultipartFile;

/**
 * Kontrak penyimpanan foto. Service hanya bergantung pada interface ini, sehingga lokasi
 * penyimpanan bisa diganti tanpa mengubah kode service: PenyimpananFotoLokal (folder laptop)
 * atau PenyimpananFotoDatabase (saat online), dipilih lewat balikinaja.penyimpanan-foto.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
public interface PenyimpananFoto {

    Foto simpan(MultipartFile file);

    /** Menyimpan gambar yang sudah berupa byte (dipakai DataSeeder untuk foto contoh). */
    Foto simpan(byte[] isi, String namaAsli, String tipeFile);

    void hapus(Foto foto);

    boolean validasiFile(MultipartFile file);
}
