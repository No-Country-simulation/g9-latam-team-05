package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.CategoriaRepository;
import com.nocountry.fintech.repository.TransaccionRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Transactional
    public TransaccionResponseDto guardar(TransaccionRequestDto dto) {
        // Validaciones obligatorias
        if (dto.getUsuarioId() == null || dto.getMonto() == null || dto.getDescripcion() == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios para registrar la transacción.");
        }

        //  Buscar entidades en BD
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        Categoria categoria = null;
        if (dto.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId()));
        } else if ("INGRESO".equalsIgnoreCase(dto.getTipo()) || "ingreso".equalsIgnoreCase(dto.getTipo())) {
            // Auto-assign "Ingresos" category
            categoria = categoriaRepository.findFirstByNombre("Ingresos")
                    .orElseGet(() -> {
                        Categoria nueva = new Categoria();
                        nueva.setNombre("Ingresos");
                        nueva.setTipo("Ingreso");
                        nueva.setColor("#10b981");
                        nueva.setIcono("arrow-trending-up");
                        return categoriaRepository.save(nueva);
                    });
        }

        // Crear y guardar transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setUsuario(usuario);
        transaccion.setCategoria(categoria);
        transaccion.setMonto(dto.getMonto());
        transaccion.setDescripcion(dto.getDescripcion());
        transaccion.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        transaccion.setTipo(dto.getTipo());

        Transaccion guardada = transaccionRepository.save(transaccion);

        // Mapear a DTO de respuesta
        return new TransaccionResponseDto(
                guardada.getId(),
                guardada.getMonto(),
                guardada.getFecha(),
                guardada.getDescripcion(),
                guardada.getTipo(),
                guardada.getCategoria() != null ? guardada.getCategoria().getNombre() : "Sin clasificar"
        );
    }

    public Page<TransaccionResponseDto> transaccionesRecientes(Long usuarioId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());

        Page<Transaccion> paginaTransacciones = transaccionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, pageable);

        return paginaTransacciones.map(t -> new TransaccionResponseDto(
                t.getId(),
                t.getMonto(),
                t.getFecha(),
                t.getDescripcion(),
                t.getTipo(),
                t.getCategoria() != null ? t.getCategoria().getNombre() : "Sin clasificar"
        ));
    }

    public void eliminar(Long id) {
        if (transaccionRepository.existsById(id)) {
            transaccionRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("No se pudo eliminar. Transacción no encontrada con ID: " + id);
        }
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
            Map<String, Object> item = new HashMap<>();
            item.put("id", t.getId());
            item.put("text", t.getDescripcion());
            item.put("monto", t.getMonto());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("transacciones", payloadTransacciones);

        // Consumir el servicio FastAPI usando RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(pythonUrl, requestBody, Map.class);

        if (response == null || !response.containsKey("clasificados")) {
            throw new RuntimeException("Error al comunicarse con el servicio de IA de Python.");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> clasificados = (List<Map<String, Object>>) response.get("clasificados");

        // Patrón de Auto-Registro en BD y agrupación
        BigDecimal montoTotalGeneral = BigDecimal.ZERO;
        Map<String, BigDecimal> montosPorCategoria = new HashMap<>();
        Map<String, Categoria> detallesCategoria = new HashMap<>();

        for (Map<String, Object> item : clasificados) {
            String nombreCategoria = (String) item.get("categoriaPredicha");
            BigDecimal monto = new BigDecimal(item.get("monto").toString());

            montoTotalGeneral = montoTotalGeneral.add(monto);
            montosPorCategoria.put(nombreCategoria, montosPorCategoria.getOrDefault(nombreCategoria, BigDecimal.ZERO).add(monto));

            // Auto-registro si la categoría no existe en BD
            if (!detallesCategoria.containsKey(nombreCategoria)) {
                Categoria categoria = categoriaRepository.findFirstByNombre(nombreCategoria)
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
        List<Map<String, Object>> resultadoFinal = new ArrayList<>();
        final BigDecimal totalFinal = montoTotalGeneral;

        montosPorCategoria.forEach((categoriaNombre, montoTotal) -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("categoria", categoriaNombre);
            dto.put("montoTotal", montoTotal);

            double porcentaje = 0.0;
            if (totalFinal.compareTo(BigDecimal.ZERO) > 0) {
                porcentaje = montoTotal.divide(totalFinal, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
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