package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.PresupuestoRequestDto;
import com.nocountry.fintech.dto.response.PresupuestoResponseDto;
import com.nocountry.fintech.service.PresupuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    @Autowired
    private PresupuestoService presupuestoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PresupuestoResponseDto>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(presupuestoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<PresupuestoResponseDto>> obtenerPorPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        return ResponseEntity.ok(presupuestoService.listarPorUsuarioYPeriodo(usuarioId, anio, mes));
    }

    @PostMapping
    public ResponseEntity<PresupuestoResponseDto> crear(@RequestBody PresupuestoRequestDto dto) {
        PresupuestoResponseDto nuevo = presupuestoService.guardar(dto);
        if (nuevo != null) {
            return ResponseEntity.ok(nuevo);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        presupuestoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
