package com.nocountry.fintech.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public record TransaccionResponseDto (
        Long id,
        BigDecimal monto,
        LocalDateTime fecha,
        String descripcion,
        String tipo
) {
}
