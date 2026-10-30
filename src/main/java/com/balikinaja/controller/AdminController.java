package com.balikinaja.controller;

import com.balikinaja.model.StatusLaporan;
import com.balikinaja.service.KlaimService;
import com.balikinaja.service.RiwayatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Dashboard Admin: ringkasan, antrean klaim, dan barang menunggu serah terima (dikerjakan bersama). */
@Controller
public class AdminController {

    private final KlaimService klaimService;
    private final RiwayatService riwayatService;

    public AdminController(KlaimService klaimService, RiwayatService riwayatService) {
        this.klaimService = klaimService;
        this.riwayatService = riwayatService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("jumlahDicari", riwayatService.hitungLaporan(StatusLaporan.DICARI));
        model.addAttribute("jumlahDitemukan", riwayatService.hitungLaporan(StatusLaporan.DITEMUKAN));
        model.addAttribute("jumlahDiklaim", riwayatService.hitungLaporan(StatusLaporan.DIKLAIM));
        model.addAttribute("jumlahSelesai", riwayatService.hitungLaporan(StatusLaporan.SELESAI));
        model.addAttribute("jumlahKlaimMenunggu", klaimService.hitungKlaimMenunggu());
        model.addAttribute("klaimMenunggu", klaimService.getKlaimMenunggu().stream().limit(5).toList());
        model.addAttribute("serahTerima", riwayatService.getLaporanMenungguSerahTerima());
        return "admin/dashboard";
    }
}
