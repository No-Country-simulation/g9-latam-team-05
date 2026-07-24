package com.nocountry.fintech.dto;

import lombok.Data;

@Data
public class UsuarioRequestDto {
    private String nombre;
    private String email;
    private String password;
}
