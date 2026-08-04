package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.PresupuestoRequestDto;
import com.nocountry.fintech.dto.response.PresupuestoResponseDto;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.model.Presupuesto;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.CategoriaRepository;
import com.nocountry.fintech.repository.PresupuestoRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
                              UsuarioRepository usuarioRepository,
                              CategoriaRepository categoriaRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public PresupuestoResponseDto guardar(PresupuestoRequestDto dto) {
        if (dto.getUsuarioId() == null || dto.getCategoriaId() == null || 
            dto.getMontoLimite() == null || dto.getMes() == null || dto.getAnio() == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios para registrar el presupuesto.");
        }

        Usuario usuario = usuarioRepository.getReferenceById(dto.getUsuarioId());
        Categoria categoria = categoriaRepository.getReferenceById(dto.getCategoriaId());

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setUsuario(usuario);
        presupuesto.setCategoria(categoria);
        presupuesto.setLimite(dto.getMontoLimite());
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());

        Presupuesto guardado = presupuestoRepository.save(presupuesto);
        return mapearAResponseDto(guardado);
    }

    public List<PresupuestoResponseDto> listarPorUsuario(Long usuarioId) {
        return presupuestoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());
    }

    public List<PresupuestoResponseDto> listarPorUsuarioYPeriodo(Long usuarioId, Integer anio, Integer mes) {
        return presupuestoRepository.findByUsuarioIdAndAnioAndMes(usuarioId, anio, mes).stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<PresupuestoResponseDto> buscarPorId(Long id) {
        return presupuestoRepository.findById(id).map(this::mapearAResponseDto);
    }

    public void eliminar(Long id) {
        if (presupuestoRepository.existsById(id)) {
            presupuestoRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se pudo eliminar. Presupuesto no encontrado con ID: " + id);
        }
    }

    private PresupuestoResponseDto mapearAResponseDto(Presupuesto p) {
        PresupuestoResponseDto dto = new PresupuestoResponseDto();
        dto.setId(p.getId());
        dto.setUsuarioId(p.getUsuario() != null ? p.getUsuario().getId() : null);
        dto.setCategoriaId(p.getCategoria() != null ? p.getCategoria().getId() : null);
        dto.setCategoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null);
        dto.setMontoLimite(p.getLimite());
        dto.setMes(p.getMes());
        dto.setAnio(p.getAnio());
        return dto;
    }
}
