package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.CategoriaRepository;
import com.nocountry.fintech.repository.TransaccionRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
        return null;
    }
//    public TransaccionResponseDto guardar(TransaccionRequestDto dto) {
//        // Validaciones básicas
//        if (dto.getUsuarioId() == null || dto.getCategoriaId() == null ||
//            dto.getMonto() == null || dto.getDescripcion() == null) {
//            System.err.println("Error: Faltan datos obligatorios para registrar la transacción.");
//            return null;
//        }
//
//        // Obtener referencias por ID (Evita SELECTs extra a la BD)
//        Usuario usuario = usuarioRepository.getReferenceById(dto.getUsuarioId());
//        Categoria categoria = categoriaRepository.getReferenceById(dto.getCategoriaId());
//
//        Transaccion transaccion = new Transaccion();
//        transaccion.setUsuario(usuario);
//        transaccion.setCategoria(categoria);
//        transaccion.setMonto(dto.getMonto());
//        transaccion.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
//        transaccion.setDescripcion(dto.getDescripcion());
//        transaccion.setTipo(dto.getTipo());
//
//        Transaccion guardada = transaccionRepository.save(transaccion);
//        return mapearAResponseDto(guardada);
//    }

        public Page<TransaccionResponseDto> transaccionesRecientes (Long usuarioId,int size){

            Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());

            Page<Transaccion> paginaTransacciones = transaccionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, pageable);
            Page<TransaccionResponseDto> listaTransacciones = paginaTransacciones.map(t -> new TransaccionResponseDto(t.getId(), t.getMonto(), t.getFecha(), t.getDescripcion(), t.getTipo()));

            return listaTransacciones;
        }

        public void eliminar (Long id){
            if (transaccionRepository.existsById(id)) {
                transaccionRepository.deleteById(id);
            } else {
                throw new ResourceNotFoundException("No se pudo eliminar. Transacción no encontrada con ID: " + id);
            }
        }
    }
