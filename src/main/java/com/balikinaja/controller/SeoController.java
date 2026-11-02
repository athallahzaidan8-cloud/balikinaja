package com.balikinaja.controller;

import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.service.LaporanTemuanService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** robots.txt dan sitemap.xml supaya halaman publik BalikinAja bisa diindeks Google. */
@RestController
public class SeoController {

    private final LaporanTemuanService laporanTemuanService;
    private final String alamatSitus;

    public SeoController(LaporanTemuanService laporanTemuanService,
                         @Value("${balikinaja.alamat-situs:}") String alamatSitus) {
        this.laporanTemuanService = laporanTemuanService;
        this.alamatSitus = alamatSitus.replaceAll("/+$", "");
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return String.join("\n",
                "User-agent: *",
                "Allow: /",
                "Disallow: /admin",
                "Disallow: /dashboard",
                "Disallow: /lapor",
                "Disallow: /laporan/saya",
                "Disallow: /laporan/hilang/",
                "Disallow: /laporan/temuan/",
                "Disallow: /klaim",
                "Sitemap: " + baseUrl() + "/sitemap.xml",
                "");
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        String base = baseUrl();
        List<String> url = new ArrayList<>(List.of("/", "/temuan", "/cari", "/register", "/login"));
        for (LaporanTemuan temuan : laporanTemuanService.getTemuanTersedia()) {
            url.add("/laporan/" + temuan.getIdLaporan());
        }
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String path : url) {
            xml.append("  <url><loc>").append(base).append(path).append("</loc></url>\n");
        }
        return xml.append("</urlset>\n").toString();
    }

    private String baseUrl() {
        if (!alamatSitus.isBlank()) {
            return alamatSitus;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
