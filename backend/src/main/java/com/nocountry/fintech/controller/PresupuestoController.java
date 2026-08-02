package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.PresupuestoRequestDto;
import com.nocountry.fintech.dto.response.PresupuestoResponseDto;
import com.nocountry.fintech.service.PresupuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService){
        this.presupuestoService = presupuestoService;
    }

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
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        presupuestoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
