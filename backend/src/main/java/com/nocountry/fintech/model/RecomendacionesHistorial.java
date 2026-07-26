package com.nocountry.fintech.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="RECOMENDACIONES_HISTORIAL")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionesHistorial {

    @Id
    @Column(name="recomendacion_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECOMENDACIONES_seq")
    @SequenceGenerator(name = "RECOMENDACIONES_seq", sequenceName = "RECOMENDACIONES_SEQ", allocationSize = 1)
    private Long id;

    @Column(name="recomendacion_texto", nullable = false, length = 500)
    private String recomendacionTexto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="analisis_historial_id", nullable = false)
    private AnalisisHistorial analisisHistorial;
}
