package com.nocountry.fintech.dto.request;

import com.nocountry.fintech.model.enums.FrecuenciaAhorro;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;

public record PerfilFinancieroRequestDTO(
        @NotNull(message="El ID del usuario es obligatorio")
        Long usuario,

        @NotNull(message="El ingreso mensual es obligatorio")
        @Positive
        BigDecimal ingresoMensual,

        @NotNull(message="El nivel de endeudamiento es obligatorio")
        BigDecimal nivelEndeudamiento,

        @NotNull(message="La frecuencia de ahorro es obligatoria")
        FrecuenciaAhorro frecuenciaAhorro
) {
}
