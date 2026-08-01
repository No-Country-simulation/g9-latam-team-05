package com.nocountry.fintech.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.math.BigDecimal;

import java.util.List;

public record AnalisisPerfilResponseDTO(
        @JsonProperty("perfil_financiero") String perfilFinanciero,
        @JsonProperty("probabilidad") double probabilidad,
        ResumenGastosDTO resumenGastos,
        List<String> recomendaciones


) {

    public record ResumenGastosDTO(
            @JsonProperty("alimentacion")
            BigDecimal alimentacion,

            @JsonProperty("transporte")
            BigDecimal transporte,

            @JsonProperty("entretenimiento")
            BigDecimal entretenimiento
    ){}

}
