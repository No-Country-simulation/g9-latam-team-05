package com.nocountry.fintech.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PresupuestoRequestDto {
    private Long usuarioId;
    private Long categoriaId;
    private BigDecimal montoLimite;
    private Integer mes;
    private Integer anio;
}
