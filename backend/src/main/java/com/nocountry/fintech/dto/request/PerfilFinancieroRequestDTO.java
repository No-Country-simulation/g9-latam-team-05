package com.nocountry.fintech.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocountry.fintech.model.enums.FrecuenciaAhorro;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;

public record PerfilFinancieroRequestDTO(
        @JsonProperty("ingreso_mensual")
        @NotNull(message="El ingreso mensual es obligatorio")
        @Positive
        BigDecimal ingresoMensual,

        @JsonProperty("nivel_endeudamiento")
        @NotNull(message="El nivel de endeudamiento es obligatorio")
        BigDecimal nivelEndeudamiento,

        @JsonProperty("frecuencia_ahorro")
        @NotNull(message="La frecuencia de ahorro es obligatoria")
        FrecuenciaAhorro frecuenciaAhorro
) {
}
