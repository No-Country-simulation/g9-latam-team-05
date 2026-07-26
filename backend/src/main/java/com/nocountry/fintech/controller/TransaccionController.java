package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.TransaccionRequestDto;
import com.nocountry.fintech.dto.TransaccionResponseDto;
import com.nocountry.fintech.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService service;

    @GetMapping
    public ResponseEntity<List<TransaccionResponseDto>> obtenerTodas() {
        return ResponseEntity.ok(service.listarTodo());
    }

    @PostMapping
    public ResponseEntity<TransaccionResponseDto> crear(@RequestBody TransaccionRequestDto tDto) {
        TransaccionResponseDto nueva = service.guardar(tDto);
        if (nueva != null) {
            return ResponseEntity.ok(nueva);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}