package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.AnalisisPerfilRequestDTO;
import com.nocountry.fintech.dto.response.AnalisisPerfilResponseDTO;
import com.nocountry.fintech.service.AnalisisPerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analisis-financiero")
public class AnalisisController {

    private final AnalisisPerfilService analisisPerfilService;

    @Autowired
    public AnalisisController(AnalisisPerfilService analisisPerfilService) {
        this.analisisPerfilService = analisisPerfilService;
    }

    @PostMapping
    public ResponseEntity<AnalisisPerfilResponseDTO> calcularAnalisis(@RequestBody AnalisisPerfilRequestDTO analisisPerfilRequestDTO){
        AnalisisPerfilResponseDTO analisisPerfilResponseDTO = analisisPerfilService.analisis(analisisPerfilRequestDTO);

        return ResponseEntity.ok(analisisPerfilResponseDTO);
    }


}
