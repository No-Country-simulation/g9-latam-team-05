package com.nocountry.fintech.model;

import com.nocountry.fintech.model.enums.FrecuenciaAhorro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ANALISIS_HISTORIAL")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalisisHistorial {

    @Id
    @Column(name="analisis_historial_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANALISIS_HISTORIAL_seq")
    @SequenceGenerator(name = "ANALISIS_HISTORIAL_seq", sequenceName = "ANALISIS_HISTORIAL_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Usuario usuario;

    @Column(name="ingreso_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresoMensual;

    @Column(name="nivel_endeudamiento", nullable = false, precision = 5, scale = 2)
    private BigDecimal nivelEndeudamiento;

    @Enumerated(EnumType.STRING)
    @Column(name="frecuencia_ahorro", nullable = false, length = 20)
    private FrecuenciaAhorro frecuenciaAhorro;

    @Column(name="perfil_resultado", nullable = false, length = 30)
    private String perfilResultado;

    @Column(name="probabilidad" , nullable = false)
    private Double probabilidad;

    @Column(name="fecha_analisis", nullable = false)
    private LocalDateTime fechaAnalisis;

    @OneToMany(mappedBy = "analisisHistorial", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecomendacionesHistorial> recomendaciones = new ArrayList<>();

    public void agregarRecomendacion(RecomendacionesHistorial recomendacion){
        recomendaciones.add(recomendacion);
        recomendacion.setAnalisisHistorial(this);
    }

    public void removerRecomendacion(RecomendacionesHistorial recomendacion){
        recomendaciones.remove(recomendacion);
        recomendacion.setAnalisisHistorial(null);
    }

}
