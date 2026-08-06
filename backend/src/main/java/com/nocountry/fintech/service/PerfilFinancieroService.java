package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.PerfilFinancieroRequestDTO;
import com.nocountry.fintech.dto.response.PerfilFinancieroResponseDTO;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import com.nocountry.fintech.model.PerfilesFinancieros;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.PerfilesFinancierosRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PerfilFinancieroService {

    private final PerfilesFinancierosRepository perfilesRepository;
    private final UsuarioRepository usuarioRepository;

    public PerfilFinancieroService(PerfilesFinancierosRepository perfilesRepository, UsuarioRepository usuarioRepository) {
        this.perfilesRepository = perfilesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener perfil por ID de usuario
    public PerfilFinancieroResponseDTO obtenerPorUsuarioId(Long usuarioId) {
        PerfilesFinancieros perfil = perfilesRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil financiero no encontrado para el usuario con ID: " + usuarioId));

        return mapearADto(perfil);
    }

    // Registrar perfil inicial extrayendo el usuario del Token JWT
    @Transactional
    public PerfilFinancieroResponseDTO registrarPerfil(String emailUsuario, PerfilFinancieroRequestDTO requestDto) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        if (perfilesRepository.findByUsuarioId(usuario.getId()).isPresent()) {
            throw new IllegalStateException("El usuario ya cuenta con un perfil financiero registrado. Utilice PUT para actualizar.");
        }

        PerfilesFinancieros perfil = new PerfilesFinancieros();
        perfil.setUsuario(usuario);
        perfil.setIngresoMensual(requestDto.ingresoMensual());
        perfil.setNivelEndeudamiento(requestDto.nivelEndeudamiento());
        perfil.setFrecuenciaAhorro(requestDto.frecuenciaAhorro());
        perfil.setFechaActualizacion(LocalDateTime.now());

        PerfilesFinancieros guardado = perfilesRepository.save(perfil);
        return mapearADto(guardado);
    }

    // Actualizar perfil existente extrayendo usuario del Token JWT
    @Transactional
    public PerfilFinancieroResponseDTO actualizarPerfil(String emailUsuario, PerfilFinancieroRequestDTO requestDto) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        PerfilesFinancieros perfil = perfilesRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un perfil financiero para actualizar."));

        perfil.setIngresoMensual(requestDto.ingresoMensual());
        perfil.setNivelEndeudamiento(requestDto.nivelEndeudamiento());
        perfil.setFrecuenciaAhorro(requestDto.frecuenciaAhorro());
        perfil.setFechaActualizacion(LocalDateTime.now());

        PerfilesFinancieros actualizado = perfilesRepository.save(perfil);
        return mapearADto(actualizado);
    }

    private PerfilFinancieroResponseDTO mapearADto(PerfilesFinancieros perfil) {
        String frecuenciaStr = perfil.getFrecuenciaAhorro() != null ? perfil.getFrecuenciaAhorro().name() : null;

        return new PerfilFinancieroResponseDTO(
                perfil.getIngresoMensual(),
                perfil.getNivelEndeudamiento(),
                frecuenciaStr
        );
    }
}
