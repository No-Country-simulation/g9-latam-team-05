package com.nocountry.fintech.repository;

import com.nocountry.fintech.model.RecomendacionesHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecomendacionesHistorialRepository extends JpaRepository<RecomendacionesHistorial, Long> {
}
