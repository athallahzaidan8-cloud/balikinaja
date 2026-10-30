package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Warga kampus yang kehilangan atau menemukan barang (mahasiswa, dosen, staf).
 * Fitur 1 - Login & Registrasi (PJ: Muhammad Zaidan Athallah)
 */
@Entity
@DiscriminatorValue("PENGGUNA")
public class Pengguna extends User {

    /** NIM untuk mahasiswa, NIP untuk dosen/staf. */
    @Column(length = 30)
    private String nomorIdentitas;

    @Column(length = 20)
    private String jenisPengguna;

    protected Pengguna() {
    }

    public Pengguna(String nama, String email, String password, String noTelepon,
                    String nomorIdentitas, String jenisPengguna) {
        super(nama, email, password, noTelepon);
        this.nomorIdentitas = nomorIdentitas;
        this.jenisPengguna = jenisPengguna;
    }

    public String getNomorIdentitas() {
        return nomorIdentitas;
    }

    public String getJenisPengguna() {
        return jenisPengguna;
    }

    @Override
    public String getRole() {
        return "PENGGUNA";
    }

    @Override
    public String getHalamanDashboard() {
        return "/dashboard";
    }
}
