package com.nocountry.fintech.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record AnalisisPerfilRequestDTO(

        @JsonProperty("ingreso_mensual")
        @NotNull(message="El ingreso mensual es obligatorio")
        @Positive(message="El ingreso mensual debe ser mayor a cero")
        BigDecimal ingresoMensual,

        @JsonProperty("nivel_endeudamiento")
        @NotNull(message="El nivel de endeudamiento es obligatorio")
        @DecimalMin(value= "0.0")
        @DecimalMax(value= "100.0")
        BigDecimal nivelEndeudamiento,

        @JsonProperty("frecuencia_ahorro")
        @NotNull(message="La frecuencia de ahorro es obligatoria")
        String frecuenciaAhorro,

        @Valid
        List<TransaccionItemDTO> transacciones,

        Long userId,
        Integer mes,
        Integer anio

) {
        public record TransaccionItemDTO(

                @NotBlank(message= "La descripcion de la transccion es obligatoria")
                String descripcion,

                @JsonProperty("valor")
                @NotNull(message = "El valor es obligatorio")
                @Positive(message = "El valor de la transaccion debe ser mayor a cero")
                BigDecimal valor
        ){ }
}
