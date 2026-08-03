package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.CategoriaRequestDto;
import com.nocountry.fintech.dto.response.CategoriaResponseDto;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponseDto guardar(CategoriaRequestDto dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty() ||
            dto.getTipo() == null || dto.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y tipo son obligatorios para la categoría.");
        }

        // Evitar categoriás duplicadas
        if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalStateException("La categoría '" + dto.getNombre() + "' ya existe.");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setTipo(dto.getTipo());
        categoria.setIcono(dto.getIcono());
        categoria.setColor(dto.getColor());

        Categoria guardada = categoriaRepository.save(categoria);
        return mapearAResponseDto(guardada);
    }

    public List<CategoriaResponseDto> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<CategoriaResponseDto> buscarPorId(Long id) {
        return categoriaRepository.findById(id).map(this::mapearAResponseDto);
    }

    public List<CategoriaResponseDto> buscarPorTipo(String tipo) {
        return categoriaRepository.findByTipo(tipo).stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
        } else{
            throw new ResourceNotFoundException("No se pudo eliminar. Categoría no encontrada con ID: " + id);
        }
    }

    private CategoriaResponseDto mapearAResponseDto(Categoria c) {
        CategoriaResponseDto dto = new CategoriaResponseDto();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setTipo(c.getTipo());
        dto.setIcono(c.getIcono());
        dto.setColor(c.getColor());
        return dto; 
    }
}
