package com.nocountry.fintech.repository;

import com.nocountry.fintech.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findFirstByNombre(String nombre);
    List<Categoria> findByTipo(String tipo);
}
