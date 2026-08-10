package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.AnalisisPerfilRequestDTO;
import com.nocountry.fintech.dto.response.AnalisisHisResponseDTO;
import com.nocountry.fintech.dto.response.AnalisisPerfilResponseDTO;
import com.nocountry.fintech.service.AnalisisPerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analisis-financiero")
public class AnalisisController {

    private final AnalisisPerfilService analisisPerfilService;

    @Autowired
    public AnalisisController(AnalisisPerfilService analisisPerfilService) {
        this.analisisPerfilService = analisisPerfilService;
    }

    @PostMapping
    public ResponseEntity<AnalisisPerfilResponseDTO> calcularAnalisis(@RequestBody AnalisisPerfilRequestDTO analisisPerfilRequestDTO) {
        return ResponseEntity.ok(analisisPerfilService.analisis(analisisPerfilRequestDTO));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<AnalisisHisResponseDTO>> obtenerHistorial(Authentication authentication) {
        return ResponseEntity.ok(analisisPerfilService.obtenerHistorialPorUsuario(authentication.getName()));
    }
}
