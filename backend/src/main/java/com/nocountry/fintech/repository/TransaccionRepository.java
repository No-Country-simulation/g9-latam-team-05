package com.nocountry.fintech.repository;

import com.nocountry.fintech.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findAllByUsuarioId(Long id);

    Page<Transaccion> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT t FROM Transaccion t WHERE t.usuario.id = :usuarioId AND t.fecha BETWEEN :inicioMes AND :finMes")
    List<Transaccion> findByUsuarioIdAndFechaBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("inicioMes") LocalDateTime inicioMes,
            @Param("finMes") LocalDateTime finMes
    );

    @Query("SELECT t FROM Transaccion t WHERE t.usuario.id = :userId " +
            "AND MONTH(t.fecha) = :mes " +
            "AND YEAR(t.fecha) = :anio")
    List<Transaccion> findByUsuarioIdAndMesAndAnio(
            @Param("userId") Long userId,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio
    );
}