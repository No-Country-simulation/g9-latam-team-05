package com.nocountry.fintech.repository;

import com.nocountry.fintech.model.AnalisisHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalisisHistorialRepository extends JpaRepository<AnalisisHistorial, Long> {
    List<AnalisisHistorial> findByUsuarioId(Long usuarioId);
}
