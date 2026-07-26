package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.CategoriaRequestDto;
import com.nocountry.fintech.dto.CategoriaResponseDto;
import com.nocountry.fintech.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>> obtenerTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CategoriaResponseDto>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(categoriaService.buscarPorTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDto> crear(@RequestBody CategoriaRequestDto dto) {
        CategoriaResponseDto nueva = categoriaService.guardar(dto);
        if (nueva != null) {
            return ResponseEntity.ok(nueva);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
