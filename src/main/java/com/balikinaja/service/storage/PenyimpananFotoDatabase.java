package com.balikinaja.service.storage;

import com.balikinaja.model.Foto;
import com.balikinaja.model.IsiFoto;
import com.balikinaja.repository.IsiFotoRepository;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Menyimpan foto di database. Dipakai saat online, karena disk server hosting gratis
 * terhapus setiap kali server restart. Foto diperkecil menjadi JPEG maksimal sekitar 450 KB
 * supaya database gratis tidak cepat penuh.
 * Fitur 3 - Lapor Barang Temuan (PJ: Keysha Dhealubna Ernaldy)
 */
@Component
@ConditionalOnProperty(name = "balikinaja.penyimpanan-foto", havingValue = "database")
public class PenyimpananFotoDatabase extends PenyimpananFotoDasar {

    private static final int[] SISI_BERTAHAP = {1600, 1280, 1024, 800};
    private static final float[] KUALITAS_BERTAHAP = {0.85f, 0.7f, 0.55f};
    private static final int UKURAN_HASIL_MAKS = 450 * 1024; // aman untuk batas paket MySQL 1 MB

    private final IsiFotoRepository isiFotoRepo;

    public PenyimpananFotoDatabase(IsiFotoRepository isiFotoRepo) {
        this.isiFotoRepo = isiFotoRepo;
    }

    @Override
    public Foto simpan(MultipartFile file) {
        if (!validasiFile(file)) {
            throw new IllegalArgumentException(PESAN_TIDAK_VALID);
        }
        try (InputStream in = file.getInputStream()) {
            return simpanGambar(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Foto gagal dibaca.", e);
        }
    }

    @Override
    public Foto simpan(byte[] isi, String namaAsli, String tipeFile) {
        return simpanGambar(new ByteArrayInputStream(isi));
    }

    @Override
    public void hapus(Foto foto) {
        if (foto != null && isiFotoRepo.existsById(foto.getNamaFile())) {
            isiFotoRepo.deleteById(foto.getNamaFile());
        }
    }

    public Optional<IsiFoto> muat(String namaFile) {
        return isiFotoRepo.findById(namaFile);
    }

    private Foto simpanGambar(InputStream in) {
        try {
            BufferedImage asli = ImageIO.read(in);
            if (asli == null) {
                throw new IllegalArgumentException(PESAN_TIDAK_VALID);
            }
            byte[] jpeg = kompres(asli);
            String namaFile = buatNamaUnik("jpg");
            isiFotoRepo.save(new IsiFoto(namaFile, "image/jpeg", jpeg));
            return new Foto(namaFile, "database:" + namaFile, "image/jpeg", jpeg.length);
        } catch (IOException e) {
            throw new UncheckedIOException("Foto gagal diproses.", e);
        }
    }

    /**
     * Turunkan kualitas lalu ukuran sampai hasilnya di bawah UKURAN_HASIL_MAKS, supaya muat
     * dalam batas paket MySQL dan database gratis tidak cepat penuh.
     */
    private static byte[] kompres(BufferedImage asli) throws IOException {
        byte[] hasil = null;
        for (int sisi : SISI_BERTAHAP) {
            BufferedImage kecil = perkecil(asli, sisi);
            for (float kualitas : KUALITAS_BERTAHAP) {
                hasil = keJpeg(kecil, kualitas);
                if (hasil.length <= UKURAN_HASIL_MAKS) {
                    return hasil;
                }
            }
        }
        return hasil;
    }

    /** Diperkecil bila lebih besar dari sisiMaks; latar transparan (PNG) diganti putih. */
    private static BufferedImage perkecil(BufferedImage asli, int sisiMaks) {
        double skala = Math.min(1.0, (double) sisiMaks / Math.max(asli.getWidth(), asli.getHeight()));
        int w = Math.max(1, (int) Math.round(asli.getWidth() * skala));
        int h = Math.max(1, (int) Math.round(asli.getHeight() * skala));
        BufferedImage hasil = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = hasil.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(asli, 0, 0, w, h, null);
        g.dispose();
        return hasil;
    }

    private static byte[] keJpeg(BufferedImage gambar, float kualitas) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(kualitas);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(gambar, null, null), param);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
