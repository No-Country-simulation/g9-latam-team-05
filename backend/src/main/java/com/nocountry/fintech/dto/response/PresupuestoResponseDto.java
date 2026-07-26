package com.nocountry.fintech.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PresupuestoResponseDto {
    private Long id;
    private Long usuarioId;
    private Long categoriaId;
    private String categoriaNombre; // Para mostrar nombre en frontend
    private BigDecimal montoLimite;
    private Integer mes;
    private Integer anio;
}
