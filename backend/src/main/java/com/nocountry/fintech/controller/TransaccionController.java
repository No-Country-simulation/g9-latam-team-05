package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService service;

    @GetMapping("/usuario/{usuarioId}/recientes")
    public ResponseEntity<Page<TransaccionResponseDto>> obtenerRecientes(@PathVariable Long usuarioId,
                                                                               @RequestParam(defaultValue = "5") int limit) {

        return ResponseEntity.ok(service.transaccionesRecientes(usuarioId, limit));
    }

    @PostMapping
    public ResponseEntity<TransaccionResponseDto> crear(@RequestBody TransaccionRequestDto tDto) {
        TransaccionResponseDto nueva = service.guardar(tDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }



}