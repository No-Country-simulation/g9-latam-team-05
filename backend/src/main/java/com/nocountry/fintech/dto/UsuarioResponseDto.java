package com.nocountry.fintech.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String email;
    private LocalDateTime fechaRegistro;
}
