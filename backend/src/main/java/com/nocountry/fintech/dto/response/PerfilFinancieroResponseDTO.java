package com.nocountry.fintech.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record PerfilFinancieroResponseDTO(
        @JsonProperty("ingreso_mensual")
        BigDecimal ingresoMensual,

        @JsonProperty("nivel_endeudamiento")
        BigDecimal nivelEndeudamiento,

        @JsonProperty("frecuencia_ahorro")
        String frecuenciaAhorro
) {
}