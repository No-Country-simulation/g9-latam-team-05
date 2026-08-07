package com.nocountry.fintech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransaccionRequestDto (
        @NotBlank
        String descripcion,
        @NotNull
        BigDecimal monto,
        @NotBlank
        String tipo
){

}
