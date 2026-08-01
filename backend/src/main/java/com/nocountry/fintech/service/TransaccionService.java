package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.CategoriaRepository;
import com.nocountry.fintech.repository.TransaccionRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransaccionService {
    
    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public TransaccionService(TransaccionRepository transaccionRepository,
                              UsuarioRepository usuarioRepository,
                              CategoriaRepository categoriaRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public TransaccionResponseDto guardar(TransaccionRequestDto dto) {
        // Validaciones obligatorias
        if (dto.getUsuarioId() == null || dto.getCategoriaId() == null || 
            dto.getMonto() == null || dto.getDescripcion() == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios para registrar la transacción.");
        }

        // Obtener referencias por ID (Evita SELECTs extra a la BD)
        Usuario usuario = usuarioRepository.getReferenceById(dto.getUsuarioId());
        Categoria categoria = categoriaRepository.getReferenceById(dto.getCategoriaId());

        Transaccion transaccion = new Transaccion();
        transaccion.setUsuario(usuario);
        transaccion.setCategoria(categoria);
        transaccion.setMonto(dto.getMonto());
        transaccion.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        transaccion.setDescripcion(dto.getDescripcion());
        transaccion.setTipo(dto.getTipo());

        Transaccion guardada = transaccionRepository.save(transaccion);
        return mapearAResponseDto(guardada);
    }

    public List<TransaccionResponseDto> listarTodo() {
        return transaccionRepository.findAll().stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        if (transaccionRepository.existsById(id)) {
            transaccionRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se pudo eliminar. Transacción no encontrada con ID: " + id);
        }
    }
    
    private TransaccionResponseDto mapearAResponseDto(Transaccion t) {
        TransaccionResponseDto dto = new TransaccionResponseDto();
        dto.setId(t.getId());
        dto.setMonto(t.getMonto());
        dto.setFecha(t.getFecha());
        dto.setDescripcion(t.getDescripcion());
        dto.setTipo(t.getTipo());
        dto.setUsuarioId(t.getUsuario() != null ? t.getUsuario().getId() : null);
        dto.setCategoriaId(t.getCategoria() != null ? t.getCategoria().getId() : null);
        return dto;
    }
}