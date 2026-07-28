package com.nocountry.fintech.repository;

import com.nocountry.fintech.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    List<Presupuesto> findByUsuarioId(Long usuarioId);
    List<Presupuesto> findByUsuarioIdAndAnioAndMes(Long usuarioId, Integer anio, Integer mes);
}
