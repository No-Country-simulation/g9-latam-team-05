package com.nocountry.fintech.model;

import com.nocountry.fintech.model.enums.FrecuenciaAhorro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="PERFILES_FINANCIEROS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilesFinancieros {

    @Id
    @Column(name="perfil_financiero_id")
    private Long perfilFinancieroId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private Usuario usuario;

    @Column(name="ingreso_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresoMensual;

    @Column(name="nivel_endeudamiento", nullable= false, precision = 5, scale = 2)
    private BigDecimal nivelEndeudamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "frecuencia_ahorro", nullable = false, length = 20)
    private FrecuenciaAhorro frecuenciaAhorro;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

}
