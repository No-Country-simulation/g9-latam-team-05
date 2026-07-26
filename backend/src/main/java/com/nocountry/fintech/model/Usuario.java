package com.nocountry.fintech.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USUARIOS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "USUARIOS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Column(name = "ESTADO", length = 20)
    private String estado;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalisisHistorial> historiales = new ArrayList<>();

    //metodo helper para mantener sincornizacion bidireccional
    public void agregarAnalisis(AnalisisHistorial analisis){
        historiales.add(analisis);
        analisis.setUsuario(this);
    }

    public void removerAnalisis(AnalisisHistorial analisis) {
        historiales.remove(analisis);
        analisis.setUsuario(null);
    }
}