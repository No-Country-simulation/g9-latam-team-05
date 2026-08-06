package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.PerfilFinancieroRequestDTO;
import com.nocountry.fintech.dto.response.PerfilFinancieroResponseDTO;
import com.nocountry.fintech.service.PerfilFinancieroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfiles-financieros")
public class PerfilFinancieroController {

    private final PerfilFinancieroService perfilFinancieroService;

    public PerfilFinancieroController(PerfilFinancieroService perfilFinancieroService) {
        this.perfilFinancieroService = perfilFinancieroService;
    }

    // Obtener Perfil (GET /api/perfiles-financieros/usuario/{usuarioId})
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PerfilFinancieroResponseDTO> obtenerPerfilPorUsuario(@PathVariable Long usuarioId) {
        PerfilFinancieroResponseDTO response = perfilFinancieroService.obtenerPorUsuarioId(usuarioId);
        return ResponseEntity.ok(response);
    }

    // Registrar Perfil Inicial (POST /api/perfiles-financieros)
    @PostMapping
    public ResponseEntity<PerfilFinancieroResponseDTO> registrarPerfil(
            @Valid @RequestBody PerfilFinancieroRequestDTO requestDto,
            Authentication authentication
    ) {
        String emailUsuario = authentication.getName();
        PerfilFinancieroResponseDTO response = perfilFinancieroService.registrarPerfil(emailUsuario, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Actualizar Perfil (PUT /api/perfiles-financieros)
    @PutMapping
    public ResponseEntity<PerfilFinancieroResponseDTO> actualizarPerfil(
            @Valid @RequestBody PerfilFinancieroRequestDTO requestDto,
            Authentication authentication
    ) {
        String emailUsuario = authentication.getName();
        PerfilFinancieroResponseDTO response = perfilFinancieroService.actualizarPerfil(emailUsuario, requestDto);
        return ResponseEntity.ok(response);
    }
}
