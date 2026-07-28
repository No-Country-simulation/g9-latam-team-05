package com.nocountry.fintech.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransaccionRequestDto {
    private Long usuarioId;
    private Long categoriaId;
    private Double monto;
    private LocalDateTime fecha;
    private String descripcion;
    private String tipo; // ingreso o gasto
}
