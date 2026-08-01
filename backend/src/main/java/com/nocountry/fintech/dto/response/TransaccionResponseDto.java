package com.nocountry.fintech.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class TransaccionResponseDto {
    private Long id;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
    private String tipo;
    private Long usuarioId;
    private Long categoriaId;
}
