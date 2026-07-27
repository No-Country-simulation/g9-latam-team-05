package com.nocountry.fintech.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "PRESUPUESTOS")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presupuesto_seq")
    @SequenceGenerator(name = "presupuesto_seq", sequenceName = "PRESUPUESTOS_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORIA_ID", nullable = false)
    private Categoria categoria;

    @Column(name = "MONTO_LIMITE", nullable = false, precision = 12, scale = 2)
    private BigDecimal limite;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;
}
