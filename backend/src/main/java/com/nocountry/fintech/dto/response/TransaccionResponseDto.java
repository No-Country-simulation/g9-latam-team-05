package com.nocountry.fintech.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransaccionResponseDto (
        Long id,
        BigDecimal monto,
        LocalDateTime fecha,
        String descripcion,
        String tipo
){

}
