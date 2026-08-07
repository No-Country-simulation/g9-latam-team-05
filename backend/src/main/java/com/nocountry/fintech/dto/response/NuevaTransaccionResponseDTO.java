package com.nocountry.fintech.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NuevaTransaccionResponseDTO(
        Long id,
        BigDecimal monto,
        LocalDateTime fecha,
        String descripcion,
        String tipo,
        String categoriaNombre
) {
}
