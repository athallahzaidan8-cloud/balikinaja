package com.balikinaja.repository;

import com.balikinaja.model.Klaim;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.StatusKlaim;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KlaimRepository extends JpaRepository<Klaim, Long> {

    List<Klaim> findByStatusOrderByTanggalPengajuanAsc(StatusKlaim status);

    List<Klaim> findByPengklaimOrderByTanggalPengajuanDesc(Pengguna pengklaim);

    List<Klaim> findByLaporanTemuanOrderByTanggalPengajuanDesc(LaporanTemuan laporanTemuan);

    List<Klaim> findByLaporanTemuanAndStatus(LaporanTemuan laporanTemuan, StatusKlaim status);

    List<Klaim> findByLaporanKehilangan(LaporanKehilangan laporanKehilangan);

    List<Klaim> findByLaporanKehilanganAndStatus(LaporanKehilangan laporanKehilangan, StatusKlaim status);

    boolean existsByPengklaimAndLaporanTemuanAndStatusIn(Pengguna pengklaim, LaporanTemuan laporanTemuan,
                                                        Collection<StatusKlaim> status);

    long countByStatus(StatusKlaim status);
}
