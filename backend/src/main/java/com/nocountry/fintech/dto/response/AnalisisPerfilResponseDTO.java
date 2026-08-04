package com.nocountry.fintech.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

public record AnalisisPerfilResponseDTO(
        @JsonProperty("perfil_financiero") String perfilFinanciero,
        @JsonProperty("probabilidad") double probabilidad,

        @JsonProperty("resumen_gastos")
        Map<String,BigDecimal> resumenGastos,
        List<String> recomendaciones


) {
}
