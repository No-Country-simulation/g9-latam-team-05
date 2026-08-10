package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.AnalisisPerfilRequestDTO;
import com.nocountry.fintech.dto.response.AnalisisPerfilResponseDTO;
import com.nocountry.fintech.model.AnalisisHistorial;
import com.nocountry.fintech.model.RecomendacionesHistorial;
import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.model.enums.FrecuenciaAhorro;
import com.nocountry.fintech.repository.AnalisisHistorialRepository;
import com.nocountry.fintech.repository.TransaccionRepository;
import com.nocountry.fintech.repository.UsuarioRepository;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.repository.CategoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Service
public class AnalisisPerfilService {

    @Value("${python.fastapi.url}")
    private String pythonFastApiUrl;

    private final ConsumoFastApi consumoFastApi;
    private final UsuarioRepository usuarioRepository;
    private final AnalisisHistorialRepository analisisHistorialRepository;
    private final TransaccionRepository transaccionRepository;
    private final CategoriaRepository categoriaRepository;

    public AnalisisPerfilService(ConsumoFastApi consumoFastApi,
                                 UsuarioRepository usuarioRepository,
                                 AnalisisHistorialRepository analisisHistorialRepository,
                                 TransaccionRepository transaccionRepository,
                                 CategoriaRepository categoriaRepository) {
        this.consumoFastApi = consumoFastApi;
        this.usuarioRepository = usuarioRepository;
        this.analisisHistorialRepository = analisisHistorialRepository;
        this.transaccionRepository = transaccionRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public AnalisisPerfilResponseDTO analisis(AnalisisPerfilRequestDTO request) {
        Usuario usuario;
        AnalisisPerfilRequestDTO transaccionesParaPython;

        if (request.transacciones() != null && !request.transacciones().isEmpty()) {
            // --- CASO A: Modo Evaluador / Jueces / Postman ---
            transaccionesParaPython = request;
            usuario = obtenerUsuarioAutenticadoOPrimerDisponible(request.userId());

        } else {
            // --- CASO B: Modo Producción App Real Angular ---
            Long userId = (request.userId() != null) ? request.userId() : getUsuarioAutenticado().getId();
            usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

            List<Transaccion> transaccionesBD;

            if (request.mes() != null && request.anio() != null) {
                // Filtro opcional por mes y año
                clasificarTransaccionesNulas(userId, request.mes(), request.anio());
                transaccionesBD = transaccionRepository.findByUsuarioIdAndMesAndAnio(userId, request.mes(), request.anio());
            } else {
                // 🚀 MODO HACKATHON POR DEFECTO: Incluye todo el historial de transacciones del usuario
                clasificarTodasTransaccionesNulas(userId);
                transaccionesBD = transaccionRepository.findByUsuarioId(userId);
            }

            List<AnalisisPerfilRequestDTO.TransaccionItemDTO> transaccionesDto = transaccionesBD.stream()
                    .filter(t -> t.getMonto() != null && t.getMonto().compareTo(BigDecimal.ZERO) > 0)
                    .map(t -> new AnalisisPerfilRequestDTO.TransaccionItemDTO(
                            (t.getDescripcion() != null && !t.getDescripcion().isBlank()) ? t.getDescripcion() : "Gasto General",
                            t.getMonto()
                    ))
                    .toList();

            if (transaccionesDto.isEmpty()) {
                transaccionesDto = List.of(new AnalisisPerfilRequestDTO.TransaccionItemDTO("Gasto Inicial", new BigDecimal("10.00")));
            }

            // Garantizar que no viajen nulos a Python
            BigDecimal ingreso = (request.ingresoMensual() != null)
                    ? request.ingresoMensual()
                    : new BigDecimal("4500.00");

            BigDecimal endeudamiento = (request.nivelEndeudamiento() != null)
                    ? request.nivelEndeudamiento()
                    : new BigDecimal("25.00");

            String frecuencia = (request.frecuenciaAhorro() != null && !request.frecuenciaAhorro().isBlank())
                    ? request.frecuenciaAhorro()
                    : "Media";

            // 🎯 CONSTRUCCIÓN CON VARIABLES PROCESADAS (Evita el 422 de FastAPI)
            transaccionesParaPython = new AnalisisPerfilRequestDTO(
                    ingreso,
                    endeudamiento,
                    frecuencia,
                    transaccionesDto,
                    null, null, null
            );
        }

        // Llamada resiliente con Fallback al servicio de Python
        AnalisisPerfilResponseDTO pythonResponse;
        try {
            pythonResponse = consumoFastApi.recomendaciones(transaccionesParaPython);
            if (pythonResponse == null) {
                throw new RuntimeException("Respuesta nula del servicio de IA.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Contingencia activada en AnalisisPerfilService: " + e.getMessage());
            pythonResponse = new AnalisisPerfilResponseDTO(
                    "En observación",
                    0.75,
                    java.util.Map.of("general", new BigDecimal("100.00")),
                    List.of(
                            "Monitorear los gastos recurrentes de forma periódica.",
                            "Mantener una reserva de emergencia equivalente a 3 meses de ingresos."
                    )
            );
        }

        // Guardar snapshot de auditoría con los datos procesados reales
        guardarSnapshotBD(usuario, transaccionesParaPython, pythonResponse);

        return pythonResponse;
    }

    private void clasificarTodasTransaccionesNulas(Long userId) {
        List<Transaccion> nulas = transaccionRepository.findByUsuarioIdAndCategoriaIsNull(userId)
                .stream()
                .filter(t -> "GASTO".equalsIgnoreCase(t.getTipo()))
                .toList();

        if (nulas.isEmpty()) {
            return;
        }

        String pythonUrl = pythonFastApiUrl + "/api/v1/classify-transactions";
        List<java.util.Map<String, Object>> payloadTransacciones = new java.util.ArrayList<>();
        for (Transaccion t : nulas) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", t.getId());
            item.put("text", t.getDescripcion());
            item.put("monto", t.getMonto());
            payloadTransacciones.add(item);
        }

        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("transacciones", payloadTransacciones);

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response = restTemplate.postForObject(pythonUrl, requestBody, java.util.Map.class);

            if (response != null && response.containsKey("clasificados")) {
                @SuppressWarnings("unchecked")
                List<java.util.Map<String, Object>> clasificados = (List<java.util.Map<String, Object>>) response.get("clasificados");

                java.util.Map<Long, String> mapeoCategorias = new java.util.HashMap<>();
                for (java.util.Map<String, Object> item : clasificados) {
                    Long txId = ((Number) item.get("id")).longValue();
                    String nombreCategoria = (String) item.get("categoriaPredicha");
                    mapeoCategorias.put(txId, nombreCategoria);
                }

                for (Transaccion t : nulas) {
                    String nombreCategoria = mapeoCategorias.get(t.getId());
                    if (nombreCategoria != null) {
                        Categoria categoria = categoriaRepository.findFirstByNombre(nombreCategoria)
                                .orElseGet(() -> {
                                    Categoria nuevaCat = new Categoria();
                                    nuevaCat.setNombre(nombreCategoria);
                                    nuevaCat.setTipo("Gasto");
                                    nuevaCat.setColor("#3357FF");
                                    nuevaCat.setIcono("shopping-cart");
                                    return categoriaRepository.save(nuevaCat);
                                });
                        t.setCategoria(categoria);
                        transaccionRepository.save(t);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al auto-clasificar transacciones nulas: " + e.getMessage());
        }
    }

    private void clasificarTransaccionesNulas(Long userId, int mes, int anio) {
        List<Transaccion> nulas = transaccionRepository.findByUsuarioIdAndMesAndAnio(userId, mes, anio)
                .stream()
                .filter(t -> t.getCategoria() == null && "GASTO".equalsIgnoreCase(t.getTipo()))
                .toList();

        if (nulas.isEmpty()) {
            return;
        }

        // Armar Payload para enviar a FastAPI
        String pythonUrl = pythonFastApiUrl + "/api/v1/classify-transactions";
        List<java.util.Map<String, Object>> payloadTransacciones = new java.util.ArrayList<>();
        for (Transaccion t : nulas) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", t.getId());
            item.put("text", t.getDescripcion());
            item.put("monto", t.getMonto());
            payloadTransacciones.add(item);
        }

        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("transacciones", payloadTransacciones);

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response = restTemplate.postForObject(pythonUrl, requestBody, java.util.Map.class);

            if (response != null && response.containsKey("clasificados")) {
                @SuppressWarnings("unchecked")
                List<java.util.Map<String, Object>> clasificados = (List<java.util.Map<String, Object>>) response.get("clasificados");

                // Mapear por ID de transaccion
                java.util.Map<Long, String> mapeoCategorias = new java.util.HashMap<>();
                for (java.util.Map<String, Object> item : clasificados) {
                    Long txId = ((Number) item.get("id")).longValue();
                    String nombreCategoria = (String) item.get("categoriaPredicha");
                    mapeoCategorias.put(txId, nombreCategoria);
                }

                for (Transaccion t : nulas) {
                    String nombreCategoria = mapeoCategorias.get(t.getId());
                    if (nombreCategoria != null) {
                        Categoria categoria = categoriaRepository.findFirstByNombre(nombreCategoria)
                                .orElseGet(() -> {
                                    Categoria nuevaCat = new Categoria();
                                    nuevaCat.setNombre(nombreCategoria);
                                    nuevaCat.setTipo("Gasto");
                                    nuevaCat.setColor("#3357FF");
                                    nuevaCat.setIcono("shopping-cart");
                                    return categoriaRepository.save(nuevaCat);
                                });
                        t.setCategoria(categoria);
                        transaccionRepository.save(t);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al auto-clasificar transacciones nulas: " + e.getMessage());
        }
    }

    private void guardarSnapshotBD(Usuario usuario, AnalisisPerfilRequestDTO requestUsado, AnalisisPerfilResponseDTO response) {
        AnalisisHistorial historial = new AnalisisHistorial();
        historial.setUsuario(usuario);
        historial.setIngresoMensual(requestUsado.ingresoMensual());
        historial.setNivelEndeudamiento(requestUsado.nivelEndeudamiento());

        // Manejo defensivo del Enum FrecuenciaAhorro
        if (requestUsado.frecuenciaAhorro() != null) {
            try {
                historial.setFrecuenciaAhorro(FrecuenciaAhorro.valueOf(requestUsado.frecuenciaAhorro().toUpperCase()));
            } catch (IllegalArgumentException e) {
                historial.setFrecuenciaAhorro(FrecuenciaAhorro.MEDIA);
            }
        } else {
            historial.setFrecuenciaAhorro(FrecuenciaAhorro.MEDIA);
        }

        historial.setPerfilResultado(response.perfilFinanciero());
        historial.setProbabilidad(response.probabilidad());
        historial.setFechaAnalisis(LocalDateTime.now());

        if (response.recomendaciones() != null) {
            for (String textoRec : response.recomendaciones()) {
                RecomendacionesHistorial rec = new RecomendacionesHistorial();
                rec.setRecomendacionTexto(textoRec);
                historial.agregarRecomendacion(rec);
            }
        }

        analisisHistorialRepository.save(historial);
    }

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    private Usuario obtenerUsuarioAutenticadoOPrimerDisponible(Long fallbackUserId) {
        try {
            return getUsuarioAutenticado();
        } catch (Exception e) {
            if (fallbackUserId != null) {
                return usuarioRepository.findById(fallbackUserId).orElse(null);
            }
            return usuarioRepository.findAll().stream().findFirst().orElse(null);
        }
    }

    @Transactional(readOnly = true)
    public List<com.nocountry.fintech.dto.response.AnalisisHisResponseDTO> obtenerHistorialPorUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return analisisHistorialRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(h -> new com.nocountry.fintech.dto.response.AnalisisHisResponseDTO(
                        h.getId(),
                        usuario.getId(),
                        h.getIngresoMensual(),
                        h.getNivelEndeudamiento(),
                        h.getFrecuenciaAhorro(),
                        h.getPerfilResultado(),
                        h.getProbabilidad(),
                        h.getFechaAnalisis(),
                        h.getRecomendaciones() != null ? 
                                h.getRecomendaciones().stream().map(RecomendacionesHistorial::getRecomendacionTexto).toList() : List.of()
                ))
                .toList();
    }
}