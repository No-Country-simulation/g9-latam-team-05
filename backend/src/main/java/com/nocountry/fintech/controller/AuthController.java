package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.LoginRequestDto;
import com.nocountry.fintech.dto.request.UsuarioRequestDto;
import com.nocountry.fintech.dto.response.LoginResponseDto;
import com.nocountry.fintech.dto.response.UsuarioResponseDto;
import com.nocountry.fintech.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDto> registrar(@RequestBody UsuarioRequestDto requestDto) {
        UsuarioResponseDto nuevoUsuario = usuarioService.registrarUsuario(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto loginResponse = usuarioService.autenticar(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
