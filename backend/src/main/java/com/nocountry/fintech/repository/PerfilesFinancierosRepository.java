package com.nocountry.fintech.repository;

import com.nocountry.fintech.model.PerfilesFinancieros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilesFinancierosRepository extends JpaRepository<PerfilesFinancieros, Long> {
    Optional<PerfilesFinancieros> findByUsuarioId(Long usuarioId);
}
