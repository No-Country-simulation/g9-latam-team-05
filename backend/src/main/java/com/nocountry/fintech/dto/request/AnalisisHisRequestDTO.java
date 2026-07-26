package com.nocountry.fintech.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AnalisisHisRequestDTO(
        @NotNull(message="El id del usuario es obligatorio")
        Long usuario,

        @NotNull(message="El ingreso mensual es obligatorio")
        @Positive(message="El ingreso mensual debe ser mayor a cero")
        BigDecimal ingresoMensual,

        @NotNull(message="El nivel de endeudamiento es obligatorio")
        @DecimalMin(value= "0.0")
        @DecimalMax(value= "100.0")
        BigDecimal nivelEndeudamiento,

        @NotNull(message="La frecuencia de ahorro es obligatoria")
        BigDecimal frecuenciaAhorro

) {
}
