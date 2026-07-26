package com.nocountry.fintech.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACCIONES")
@Data
@NoArgsConstructor
public class Transaccion {
    // Configura la generación de IDs por secuencia explícita
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaccion_seq")
    @SequenceGenerator(name = "transaccion_seq", sequenceName = "TRANSACCIONES_SEQ", allocationSize = 1)
    private Long id;

    // -------- Para consultas: transaccion.getUsuario().getEmail()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Usuario usuario;

    // -------- Para consultas: transaccion.getCategoria().getNombre()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORIA_ID", nullable = false)
    private Categoria categoria;


    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;

    @Column(name = "MONTO", nullable = false)
    private Double monto;

    @Column(name = "TIPO", nullable = false)
    private String tipo;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
}