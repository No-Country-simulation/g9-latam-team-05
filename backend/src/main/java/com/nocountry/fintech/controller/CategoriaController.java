package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.request.CategoriaRequestDto;
import com.nocountry.fintech.dto.response.CategoriaResponseDto;
import com.nocountry.fintech.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

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
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
