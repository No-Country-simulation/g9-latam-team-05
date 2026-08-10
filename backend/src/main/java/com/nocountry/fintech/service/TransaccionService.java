package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.response.NuevaTransaccionResponseDTO;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.repository.CategoriaRepository;
import com.nocountry.fintech.repository.TransaccionRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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

    @Value("${python.fastapi.url}")
    private String pythonFastApiUrl;

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

    public Map<String, Object> obtenerDistribucionPorUsuario(Long usuarioId) {
        // 1. Verificar estado REAL del microservicio de IA Python a través de un Health Check ultra-rápido (800ms max)
        boolean pythonOnline = checkPythonHealth();
        boolean modoContingencia = !pythonOnline;
        String mensajeEstado;

        if (pythonOnline) {
            mensajeEstado = "Servicio de IA Python Online";
        } else {
            mensajeEstado = "Servicio de IA no disponible (Python Offline). Mostrando datos de BD.";
            System.err.println("⚠️ Contingencia activada: Microservicio Python en " + pythonFastApiUrl + " está Offline.");
        }

        // 2. Filtrar únicamente las transacciones del usuario que faltan clasificar (categoria == null)
        List<Transaccion> pendientes = transaccionRepository.findByUsuarioIdAndCategoriaIsNull(usuarioId);

        // 3. Si hay transacciones pendientes y Python está Online, procesarlas con la IA
        if (!pendientes.isEmpty() && pythonOnline) {
            String pythonUrl = pythonFastApiUrl + "/api/v1/classify-transactions";

            List<Map<String, Object>> payloadTransacciones = pendientes.stream().map(t -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", t.getId());
                item.put("text", t.getDescripcion());
                item.put("monto", t.getMonto());
                return item;
            }).collect(Collectors.toList());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("transacciones", payloadTransacciones);

            try {
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(3000);
                factory.setReadTimeout(5000);
                RestTemplate restTemplate = new RestTemplate(factory);

                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.postForObject(pythonUrl, requestBody, Map.class);

                if (response != null && response.containsKey("clasificados")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> clasificados = (List<Map<String, Object>>) response.get("clasificados");

                    Map<Long, String> prediccionesPorId = new HashMap<>();
                    for (Map<String, Object> item : clasificados) {
                        Long id = Long.valueOf(item.get("id").toString());
                        String catName = (String) item.get("categoriaPredicha");
                        prediccionesPorId.put(id, catName);
                    }

                    for (Transaccion tx : pendientes) {
                        String catNombre = prediccionesPorId.get(tx.getId());
                        if (catNombre != null) {
                            String[] estilo = obtenerEstiloCategoria(catNombre);
                            Categoria cat = categoriaRepository.findFirstByNombre(catNombre)
                                    .orElseGet(() -> {
                                        Categoria nuevaCat = new Categoria();
                                        nuevaCat.setNombre(catNombre);
                                        nuevaCat.setTipo("Gasto");
                                        nuevaCat.setColor(estilo[0]);
                                        nuevaCat.setIcono(estilo[1]);
                                        return categoriaRepository.save(nuevaCat);
                                    });
                            tx.setCategoria(cat);
                            transaccionRepository.save(tx);
                        }
                    }
                }
            } catch (Exception e) {
                modoContingencia = true;
                mensajeEstado = "Falló la clasificación con la IA de Python. Mostrando datos de BD.";
                System.err.println("⚠️ Error al clasificar con Python: " + e.getMessage());
            }
        }

        // 4. Consultar todas las transacciones del usuario (persistidas en BD) y calcular la distribución
        List<Transaccion> todas = transaccionRepository.findByUsuarioId(usuarioId);
        if (todas.isEmpty()) {
            Map<String, Object> respuestaVacia = new HashMap<>();
            respuestaVacia.put("modoContingencia", modoContingencia);
            respuestaVacia.put("mensajeEstado", mensajeEstado);
            respuestaVacia.put("distribucion", List.of());
            return respuestaVacia;
        }

        BigDecimal montoTotalGeneral = BigDecimal.ZERO;
        Map<String, BigDecimal> montosPorCategoria = new HashMap<>();
        Map<String, Categoria> detallesCategoria = new HashMap<>();

        for (Transaccion t : todas) {
            if ("INGRESO".equalsIgnoreCase(t.getTipo())) continue;

            String nombreCategory = t.getCategoria() != null ? t.getCategoria().getNombre() : "Sin clasificar";
            BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;

            montoTotalGeneral = montoTotalGeneral.add(monto);
            montosPorCategoria.put(nombreCategory, montosPorCategoria.getOrDefault(nombreCategory, BigDecimal.ZERO).add(monto));

            if (t.getCategoria() != null && !detallesCategoria.containsKey(nombreCategory)) {
                detallesCategoria.put(nombreCategory, t.getCategoria());
            }
        }

        List<Map<String, Object>> listaDistribucion = new ArrayList<>();
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
            String[] estiloDefecto = obtenerEstiloCategoria(categoriaNombre);
            
            String color = (catInfo != null && catInfo.getColor() != null && !"#3357FF".equals(catInfo.getColor())) 
                    ? catInfo.getColor() : estiloDefecto[0];
            String icono = (catInfo != null && catInfo.getIcono() != null && !"shopping-cart".equals(catInfo.getIcono())) 
                    ? catInfo.getIcono() : estiloDefecto[1];

            dto.put("color", color);
            dto.put("icono", icono);

            listaDistribucion.add(dto);
        });

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("modoContingencia", modoContingencia);
        respuesta.put("mensajeEstado", mensajeEstado);
        respuesta.put("distribucion", listaDistribucion);

        return respuesta;
    }

    private String[] obtenerEstiloCategoria(String nombre) {
        if (nombre == null) return new String[]{"#64748b", "tag"};
        String n = nombre.toLowerCase().trim();
        if (n.contains("vivienda") || n.contains("alquiler") || n.contains("renta")) {
            return new String[]{"#f59e0b", "home"}; // Amber
        } else if (n.contains("alimentacion") || n.contains("comida") || n.contains("super") || n.contains("mercado")) {
            return new String[]{"#3b82f6", "shopping-cart"}; // Blue
        } else if (n.contains("transporte") || n.contains("gasolina") || n.contains("uber") || n.contains("combustible")) {
            return new String[]{"#10b981", "bus"}; // Emerald
        } else if (n.contains("entretenimiento") || n.contains("ocio") || n.contains("netflix") || n.contains("cine")) {
            return new String[]{"#ec4899", "film"}; // Pink
        } else if (n.contains("salud") || n.contains("farmacia") || n.contains("medico")) {
            return new String[]{"#ef4444", "heart"}; // Red
        } else if (n.contains("servicio") || n.contains("luz") || n.contains("agua") || n.contains("internet")) {
            return new String[]{"#8b5cf6", "bolt"}; // Purple
        } else if (n.contains("educacion") || n.contains("curso") || n.contains("libro")) {
            return new String[]{"#06b6d4", "academic-cap"}; // Cyan
        } else if (n.contains("ingreso")) {
            return new String[]{"#22c55e", "arrow-trending-up"}; // Green
        }
        return new String[]{"#64748b", "tag"};
    }

    private boolean checkPythonHealth() {
        try {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(800); // Max 800ms health check timeout
            factory.setReadTimeout(800);
            RestTemplate restTemplate = new RestTemplate(factory);

            String healthUrl = pythonFastApiUrl + "/health";
            Map<?, ?> res = restTemplate.getForObject(healthUrl, Map.class);
            return res != null && "ok".equalsIgnoreCase(String.valueOf(res.get("status")));
        } catch (Exception e) {
            return false;
        }
    }

    public NuevaTransaccionResponseDTO registrarTransaccion(TransaccionRequestDto dto ) {
        //Extraemos usuario
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String emailUsuario = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //Crear registro
        Transaccion transaccion = new Transaccion();
        transaccion.setMonto(dto.monto());
        transaccion.setDescripcion(dto.descripcion());
        transaccion.setTipo(dto.tipo());
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setUsuario(usuario);
        transaccion.setCategoria(null);


        Transaccion guardada = transaccionRepository.save(transaccion);

        return new NuevaTransaccionResponseDTO(
                usuario.getId(),
                transaccion.getMonto(),
                LocalDateTime.now(),
                dto.descripcion(),
                dto.tipo(),
                "Sin clasificar");
    }
}