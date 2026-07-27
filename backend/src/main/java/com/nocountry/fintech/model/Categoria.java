package com.nocountry.fintech.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CATEGORIAS")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoria_seq")
    @SequenceGenerator(name = "categoria_seq", sequenceName = "CATEGORIAS_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(name = "ICONO", length = 50)
    private String icono;

    @Column(name = "COLOR", length = 7)
    private String color;
}
