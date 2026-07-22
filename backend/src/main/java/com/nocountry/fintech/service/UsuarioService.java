package com.nocountry.fintech.service;

import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registrar usuario nuevo (validaciones y control de duplicadoss)
    public Usuario registrarUsuario(String nombre, String email, String passwordHash, String estado) {
        try {
            // Validaciones obligatorias de campos vacios o nulos
            if (nombre == null || nombre.trim().isEmpty() || 
                email == null || email.trim().isEmpty() || 
                passwordHash == null || passwordHash.trim().isEmpty()) {
                System.err.println("Error de validación: Nombre, email y contraseña son obligatorios.");
                return null;
            }

            // Validar si el correo ya está registrado para evitar la excepción ORA-00001
            if (usuarioRepository.findByEmail(email).isPresent()) {
                System.err.println("Error: El correo electrónico '" + email + "' ya se encuentra registrado.");
                return null;
            }

            // Crear y guardar el nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPasswordHash(passwordHash);
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setEstado(estado != null ? estado : "ACTIVO");
            
            Usuario guardado = usuarioRepository.save(usuario);
            System.out.println("Usuario registrado correctamente con ID: " + guardado.getId());
            return guardado;

        } catch (Exception e) {
            System.err.println("Error: No se pudo registrar el usuario. Verifique los datos.");
            return null;
        }
    }

    public List<Usuario> listarUsuarios() {
        try {
            return usuarioRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error: No se pudieron recuperar los usuarios.");
            return List.of();
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        try {
            return usuarioRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error al buscar el usuario con ID " + id);
            return Optional.empty();
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        try {
            return usuarioRepository.findByEmail(email);
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
}