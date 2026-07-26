package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.UsuarioRequestDto;
import com.nocountry.fintech.dto.UsuarioResponseDto;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> obtenerTodos() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable Long id) {
        Optional<UsuarioResponseDto> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crear(@RequestBody UsuarioRequestDto usuarioDto) {
        UsuarioResponseDto nuevoUsuario = usuarioService.registrarUsuario(usuarioDto);
        if (nuevoUsuario != null) {
            return ResponseEntity.ok(nuevoUsuario);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}