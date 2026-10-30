package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Petugas yang memverifikasi klaim dan menutup laporan.
 * Fitur 1 - Login & Registrasi (PJ: Muhammad Zaidan Athallah)
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(length = 100)
    private String unitKerja;

    protected Admin() {
    }

    public Admin(String nama, String email, String password, String noTelepon, String unitKerja) {
        super(nama, email, password, noTelepon);
        this.unitKerja = unitKerja;
    }

    public String getUnitKerja() {
        return unitKerja;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getHalamanDashboard() {
        return "/admin";
    }
}
