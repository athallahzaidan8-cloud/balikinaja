package com.balikinaja.repository;

import com.balikinaja.model.Laporan;
import com.balikinaja.model.LaporanKehilangan;
import com.balikinaja.model.LaporanTemuan;
import com.balikinaja.model.Pengguna;
import com.balikinaja.model.StatusLaporan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LaporanRepository extends JpaRepository<Laporan, Long> {

    List<Laporan> findAllByOrderByIdLaporanDesc();

    List<Laporan> findByPelaporOrderByIdLaporanDesc(Pengguna pelapor);

    List<Laporan> findByStatusOrderByIdLaporanDesc(StatusLaporan status);

    long countByStatus(StatusLaporan status);

    @Query("select l from LaporanKehilangan l where l.pelapor = :pelapor order by l.idLaporan desc")
    List<LaporanKehilangan> findKehilanganByPelapor(Pengguna pelapor);

    @Query("select l from LaporanKehilangan l where l.pelapor = :pelapor and l.status = :status order by l.idLaporan desc")
    List<LaporanKehilangan> findKehilanganByPelaporAndStatus(Pengguna pelapor, StatusLaporan status);

    @Query("select l from LaporanTemuan l where l.status = :status order by l.idLaporan desc")
    List<LaporanTemuan> findTemuanByStatus(StatusLaporan status);
}
