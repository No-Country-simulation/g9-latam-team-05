package com.nocountry.fintech.dto.response;

import com.nocountry.fintech.model.enums.FrecuenciaAhorro;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AnalisisHisResponseDTO(
        Long id,
        Long userId,
        BigDecimal ingresoMensual,
        BigDecimal nivelEndeudamiento,
        FrecuenciaAhorro frecuenciaAhorro,
        String perfilResultado,
        Double probabilidad,
        LocalDateTime fechaAnalisis,
        List<String> recomendaciones
) {

}
