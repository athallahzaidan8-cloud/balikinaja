package com.balikinaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Kelas abstrak induk semua akun.
 * Fitur 1 - Login & Registrasi (PJ: Muhammad Zaidan Athallah)
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long idUser;

    @Column(nullable = false, length = 100)
    protected String nama;

    @Column(nullable = false, unique = true, length = 120)
    protected String email;

    // private: kelas turunan pun tidak boleh membaca password, cukup lewat cekPassword()
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 20)
    protected String noTelepon;

    protected User() {
    }

    /** password yang diterima di sini sudah berupa hash BCrypt (lihat AuthService). */
    public User(String nama, String email, String password, String noTelepon) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.noTelepon = noTelepon;
    }

    public boolean cekPassword(String password) {
        return password != null && BCrypt.checkpw(password, this.password);
    }

    public Long getIdUser() {
        return idUser;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public String getNamaDepan() {
        return nama.split(" ")[0];
    }

    public abstract String getRole();

    public abstract String getHalamanDashboard();
}
