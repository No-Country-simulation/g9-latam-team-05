package com.nocountry.fintech.dto.response;

import java.math.BigDecimal;

public record ResumenKpiDTO(
        BigDecimal ingresosMensuales,
        BigDecimal gastosTotales,
        BigDecimal balanceNeto,
        Double tasaAhorro
) {
}
