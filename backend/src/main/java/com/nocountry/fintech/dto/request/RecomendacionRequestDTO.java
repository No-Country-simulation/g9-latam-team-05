package com.nocountry.fintech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecomendacionRequestDTO(

        @NotBlank(message="El texto de la recomendacion no estar vacio")
        @Size(max = 1000, message="La recomedacion no puede superar 1000 caracteres")
        String recomendacionTexto
) {
}
