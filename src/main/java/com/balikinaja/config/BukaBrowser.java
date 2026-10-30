package com.balikinaja.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Setelah aplikasi siap, tampilkan alamatnya di log dan buka browser otomatis.
 * Matikan dengan balikinaja.buka-browser=false.
 */
@Component
public class BukaBrowser {

    private static final Logger log = LoggerFactory.getLogger(BukaBrowser.class);

    private final boolean aktif;
    private final int port;

    public BukaBrowser(@Value("${balikinaja.buka-browser:true}") boolean aktif,
                       @Value("${server.port:8080}") int port) {
        this.aktif = aktif;
        this.port = port;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void bukaSaatSiap() {
        String url = "http://localhost:" + port;
        log.info("==> BalikinAja berjalan. Buka {} di browser.", url);
        for (String ip : alamatJaringan()) {
            log.info("==> Perangkat lain di Wi-Fi/hotspot yang sama bisa membuka http://{}:{}", ip, port);
        }
        // System property bertahan saat DevTools me-restart aplikasi, jadi browser hanya dibuka sekali
        if (!aktif || System.getProperty("balikinaja.browserDibuka") != null) {
            return;
        }
        System.setProperty("balikinaja.browserDibuka", "ya");
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        try {
            if (os.contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception e) {
            log.info("Browser tidak bisa dibuka otomatis. Silakan buka {} secara manual.", url);
        }
    }

    /** Alamat IPv4 laptop di jaringan lokal (Wi-Fi/LAN), tanpa adapter virtual. */
    private static List<String> alamatJaringan() {
        List<String> hasil = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                String nama = ni.getDisplayName().toLowerCase(Locale.ROOT);
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()
                        || nama.contains("virtual") || nama.contains("vmware") || nama.contains("hyper-v")) {
                    continue;
                }
                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLinkLocalAddress()) {
                        hasil.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            // tanpa daftar IP pun aplikasi tetap jalan
        }
        return hasil;
    }
}
