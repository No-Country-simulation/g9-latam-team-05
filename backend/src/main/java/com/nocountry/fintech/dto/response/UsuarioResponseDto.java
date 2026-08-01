package com.nocountry.fintech.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@JsonPropertyOrder({ "id", "nombre", "email", "estado", "fechaRegistro" })
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String email;
    private LocalDateTime fechaRegistro;
    private String estado;
}
