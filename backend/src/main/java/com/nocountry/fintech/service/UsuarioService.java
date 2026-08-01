package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.UsuarioRequestDto;
import com.nocountry.fintech.dto.response.UsuarioResponseDto;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registrar usuario nuevo (validaciones y control de duplicadoss)
    public UsuarioResponseDto registrarUsuario(UsuarioRequestDto dto) {
        
        // Validaciones obligatorias
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty() || 
            dto.getEmail() == null || dto.getEmail().trim().isEmpty() || 
            dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre, email y contraseña son obligatorios.");
        }

        // Validar duplicidad de email
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("El correo electrónico '" + dto.getEmail() + "' ya se encuentra registrado.");
        }


        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPasswordHash(dto.getPassword()); 
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado("ACTIVO");
            
            
        Usuario guardado = usuarioRepository.save(usuario);
        return mapearAResponseDto(guardado);
    }

    public List<UsuarioResponseDto> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());  
    }

    public Optional<UsuarioResponseDto> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::mapearAResponseDto);
    }

    public Optional<UsuarioResponseDto> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                    .map(this::mapearAResponseDto);
    }
    
    public void eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("No se pudo eliminar. Usuario no encontrado con ID: " + id);
        }
    }

    // Transformar Entidad a Response DTO para evitar exponer passwordHash
    private UsuarioResponseDto mapearAResponseDto(Usuario usuario) {
        UsuarioResponseDto responseDto = new UsuarioResponseDto();
        responseDto.setId(usuario.getId());
        responseDto.setNombre(usuario.getNombre());
        responseDto.setEmail(usuario.getEmail());
        responseDto.setFechaRegistro(usuario.getFechaRegistro());
        return responseDto;
    }


}