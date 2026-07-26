package com.nocountry.fintech.dto;

import lombok.Data;

@Data
public class CategoriaResponseDto {
    private Long id;
    private String nombre;
    private String tipo;
    private String icono;
    private String color;
}
