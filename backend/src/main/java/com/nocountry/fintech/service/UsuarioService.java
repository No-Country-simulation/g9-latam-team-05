package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.UsuarioRequestDto;
import com.nocountry.fintech.dto.response.UsuarioResponseDto;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        try {
            // Validaciones obligatorias
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty() || 
                dto.getEmail() == null || dto.getEmail().trim().isEmpty() || 
                dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                System.err.println("Error de validación: Nombre, email y contraseña son obligatorios.");
                return null;
            }

            // Validar duplicidad de email
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                System.err.println("Error: El correo electrónico '" + dto.getEmail() + "' ya se encuentra registrado.");
                return null;
            }


            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setEmail(dto.getEmail());
            usuario.setPasswordHash(dto.getPassword()); 
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setEstado("ACTIVO");
            
            
            Usuario guardado = usuarioRepository.save(usuario);
            System.out.println("Usuario registrado correctamente con ID: " + guardado.getId());
            return mapearAResponseDto(guardado);

        } catch (Exception e) {
            System.err.println("Error: No se pudo registrar el usuario. Verifique los datos.");
            return null;
        }
    }

    public List<UsuarioResponseDto> listarUsuarios() {
        try {
            return usuarioRepository.findAll().stream()
                    .map(this::mapearAResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error: No se pudieron recuperar los usuarios.");
            return List.of();
        }
    }

    public Optional<UsuarioResponseDto> buscarPorId(Long id) {
        try {
            return usuarioRepository.findById(id).map(this::mapearAResponseDto);
        } catch (Exception e) {
            System.err.println("Error al buscar el usuario con ID " + id);
            return Optional.empty();
        }
    }

    public Optional<UsuarioResponseDto> buscarPorEmail(String email) {
        try {
            return usuarioRepository.findByEmail(email)
                    .map(this::mapearAResponseDto);
        } catch (Exception e) {
            System.err.println("Error al buscar el usuario por email.");
            return Optional.empty();
        }
    }
    
    public void eliminarUsuario(Long id) {
        try {
            if (usuarioRepository.existsById(id)) {
                usuarioRepository.deleteById(id);
                System.out.println("Usuario con ID " + id + " eliminado correctamente.");
            } else {
                System.out.println("Aviso: No se encontró ningún usuario con el ID " + id + ".");
            }
        } catch (Exception e) {
            System.err.println("Erro: No se pudo eliminar el usuario");
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