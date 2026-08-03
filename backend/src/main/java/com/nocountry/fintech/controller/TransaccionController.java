package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService){
        this.transaccionService = transaccionService;
    }

    @GetMapping("/usuario/{usuarioId}/recientes")
    public ResponseEntity<Page<TransaccionResponseDto>> obtenerRecientes(@PathVariable Long usuarioId,
                                                                               @RequestParam(defaultValue = "5") int limit) {

        return ResponseEntity.ok(transaccionService.transaccionesRecientes(usuarioId, limit));
    }

    @PostMapping
    public ResponseEntity<TransaccionResponseDto> crear(@RequestBody TransaccionRequestDto tDto) {
        TransaccionResponseDto nueva = transaccionService.guardar(tDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transaccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}/distribucion")
    public ResponseEntity<List<Map<String, Object>>> obtenerDistribucion(@PathVariable Long usuarioId) {
        List<Map<String, Object>> distribucion = transaccionService.obtenerDistribucionPorUsuario(usuarioId);
        return ResponseEntity.ok(distribucion);
    }

}