package com.nocountry.fintech.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransaccionResponseDto {
    private Long id;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;
    private String tipo;
    private Long usuarioId;
    
}
