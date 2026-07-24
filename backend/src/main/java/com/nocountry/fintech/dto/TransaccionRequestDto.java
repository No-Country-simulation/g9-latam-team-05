package com.nocountry.fintech.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransaccionRequestDto {
    private Long usuarioId;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;
    private String tipo; // ingreso o gasto
}
