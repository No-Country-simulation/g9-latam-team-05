package com.nocountry.fintech.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record PythonRequestDTO(
        @JsonProperty("ingreso_mensual") BigDecimal ingresoMensual,
        @JsonProperty("nivel_endeudamiento") BigDecimal nivelEndeudamiento,
        @JsonProperty("frecuencia_ahorro") String frecuenciaAhorro,
        List<AnalisisPerfilRequestDTO.TransaccionItemDTO> transacciones) {
}
