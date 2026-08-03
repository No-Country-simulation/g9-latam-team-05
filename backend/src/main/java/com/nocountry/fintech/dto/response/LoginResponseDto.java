package com.nocountry.fintech.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String tokenType;
    private UsuarioSimpleDto usuario;

    @Data
    @JsonPropertyOrder({ "id", "nombre", "email" })
    public static class UsuarioSimpleDto {
        private Long id;
        private String nombre;
        private String email;
    }
}
