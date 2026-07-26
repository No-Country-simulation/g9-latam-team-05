package com.nocountry.fintech.dto;

import lombok.Data;

@Data
public class CategoriaRequestDto {
    private String nombre;
    private String tipo; // Ingreso o Gasto
    private String icono;
    private String color;
}
