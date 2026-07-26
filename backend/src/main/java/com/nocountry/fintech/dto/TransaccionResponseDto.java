package com.nocountry.fintech.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransaccionResponseDto {
    private Long id;
    private Double monto;
    private LocalDateTime fecha;
    private String descripcion;
    private String tipo;
    private Long usuarioId;
    private Long categoriaId;
}
