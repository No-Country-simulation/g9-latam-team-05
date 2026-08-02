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
import java.util.Map;
import java.util.HashMap;

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

    public List<Map<String, Object>> obtenerDistribucionPorUsuario(Long usuarioId) {
        // Obtener las transacciones del usuario
        List<Transaccion> transacciones = transaccionRepository.findByUsuarioId(usuarioId);
        if (transacciones.isEmpty()) {
            return List.of();
        }

        // Armar Payload para enviar a FastAPI
        String pythonUrl = "http://localhost:8000/api/v1/classify-transactions";
        
        List<Map<String, Object>> payloadTransacciones = transacciones.stream().map(t -> {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", t.getId());
            item.put("text", t.getDescripcion());
            item.put("monto", t.getMonto());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("transacciones", payloadTransacciones);

        // Consumir el servicio FastAPI usando RestTemplate
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(pythonUrl, requestBody, Map.class);

        if (response == null || !response.containsKey("clasificados")) {
            throw new RuntimeException("Error al comunicarse con el servicio de IA de Python.");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> clasificados = (List<Map<String, Object>>) response.get("clasificados");

        // Patrón de Auto-Registro en BD y agrupación
        java.math.BigDecimal montoTotalGeneral = java.math.BigDecimal.ZERO;
        Map<String, java.math.BigDecimal> montosPorCategoria = new java.util.HashMap<>();
        Map<String, Categoria> detallesCategoria = new java.util.HashMap<>();

        for (Map<String, Object> item : clasificados) {
            String nombreCategoria = (String) item.get("categoriaPredicha");
            java.math.BigDecimal monto = new java.math.BigDecimal(item.get("monto").toString());

            montoTotalGeneral = montoTotalGeneral.add(monto);
            montosPorCategoria.put(nombreCategoria, montosPorCategoria.getOrDefault(nombreCategoria, java.math.BigDecimal.ZERO).add(monto));

            // Auto-registro si la categoría no existe en BD
            if (!detallesCategoria.containsKey(nombreCategoria)) {
                Categoria categoria = categoriaRepository.findByNombre(nombreCategoria)
                        .orElseGet(() -> {
                            Categoria nuevaCat = new Categoria();
                            nuevaCat.setNombre(nombreCategoria);
                            nuevaCat.setTipo("Gasto");
                            nuevaCat.setColor("#3357FF");
                            nuevaCat.setIcono("shopping-cart");
                            return categoriaRepository.save(nuevaCat);
                        });
                detallesCategoria.put(nombreCategoria, categoria);
            }
        }

        // Construir la respuesta final agrupada con porcentajes para Angular
        List<Map<String, Object>> resultadoFinal = new java.util.ArrayList<>();
        final java.math.BigDecimal totalFinal = montoTotalGeneral;

        montosPorCategoria.forEach((categoriaNombre, montoTotal) -> {
            Map<String, Object> dto = new java.util.HashMap<>();
            dto.put("categoria", categoriaNombre);
            dto.put("montoTotal", montoTotal);

            double porcentaje = 0.0;
            if (totalFinal.compareTo(java.math.BigDecimal.ZERO) > 0) {
                porcentaje = montoTotal.divide(totalFinal, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new java.math.BigDecimal("100"))
                        .doubleValue();
            }
            dto.put("porcentaje", porcentaje);

            Categoria catInfo = detallesCategoria.get(categoriaNombre);
            dto.put("color", catInfo.getColor() != null ? catInfo.getColor() : "#3357FF");
            dto.put("icono", catInfo.getIcono() != null ? catInfo.getIcono() : "shopping-cart");

            resultadoFinal.add(dto);
        });

        return resultadoFinal;
    }


}