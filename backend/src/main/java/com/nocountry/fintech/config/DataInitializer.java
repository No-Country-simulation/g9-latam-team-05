package com.nocountry.fintech.config;

import com.nocountry.fintech.model.*;
import com.nocountry.fintech.model.enums.FrecuenciaAhorro;
import com.nocountry.fintech.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PerfilesFinancierosRepository perfilesFinancierosRepository;
    private final TransaccionRepository transaccionRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final AnalisisHistorialRepository analisisHistorialRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           CategoriaRepository categoriaRepository,
                           PerfilesFinancierosRepository perfilesFinancierosRepository,
                           TransaccionRepository transaccionRepository,
                           PresupuestoRepository presupuestoRepository,
                           AnalisisHistorialRepository analisisHistorialRepository) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.perfilesFinancierosRepository = perfilesFinancierosRepository;
        this.transaccionRepository = transaccionRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.analisisHistorialRepository = analisisHistorialRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            System.out.println("ℹ️ DataInitializer: La base de datos ya cuenta con registros. Omitiendo poblamiento inicial.");
            return;
        }

        System.out.println("🚀 DataInitializer: Poblando las 7 tablas de la base de datos con 5 usuarios y registros mock de prueba...");

        // 1. TABLA: CATEGORIAS (5 Registros)
        List<Categoria> categorias = new ArrayList<>();
        
        Categoria c1 = new Categoria();
        c1.setNombre("Alimentación");
        c1.setTipo("GASTO");
        c1.setIcono("shopping-cart");
        c1.setColor("#3357FF");
        categorias.add(categoriaRepository.save(c1));

        Categoria c2 = new Categoria();
        c2.setNombre("Transporte");
        c2.setTipo("GASTO");
        c2.setIcono("bus");
        c2.setColor("#28A745");
        categorias.add(categoriaRepository.save(c2));

        Categoria c3 = new Categoria();
        c3.setNombre("Entretenimiento");
        c3.setTipo("GASTO");
        c3.setIcono("film");
        c3.setColor("#00BCD4");
        categorias.add(categoriaRepository.save(c3));

        Categoria c4 = new Categoria();
        c4.setNombre("Vivienda");
        c4.setTipo("GASTO");
        c4.setIcono("home");
        c4.setColor("#FFC107");
        categorias.add(categoriaRepository.save(c4));

        Categoria c5 = new Categoria();
        c5.setNombre("Salario");
        c5.setTipo("INGRESO");
        c5.setIcono("dollar-sign");
        c5.setColor("#2E7D32");
        categorias.add(categoriaRepository.save(c5));

        // 2. TABLA: USUARIOS (5 Registros)
        String[][] datosUsuarios = {
            {"Carlos Mendoza", "carlos.mendoza@nocountry.com"},
            {"Ana Gómez", "ana.gomez@nocountry.com"},
            {"Luis Torres", "luis.torres@nocountry.com"},
            {"María Rodríguez", "maria.rodriguez@nocountry.com"},
            {"Jorge Benítez", "jorge.benitez@nocountry.com"}
        };

        for (int i = 0; i < datosUsuarios.length; i++) {
            // A. Crear Usuario
            Usuario u = new Usuario();
            u.setNombre(datosUsuarios[i][0]);
            u.setEmail(datosUsuarios[i][1]);
            u.setPasswordHash("Password123!");
            u.setFechaRegistro(LocalDateTime.now().minusDays(30 - (i * 5)));
            u.setEstado("ACTIVO");
            u = usuarioRepository.save(u);

            // B. TABLA: PERFILES_FINANCIEROS (5 Registros)
            PerfilesFinancieros pf = new PerfilesFinancieros();
            pf.setUsuario(u);
            pf.setIngresoMensual(new BigDecimal("4500.00"));
            pf.setNivelEndeudamiento(new BigDecimal("25.00"));
            pf.setFrecuenciaAhorro(FrecuenciaAhorro.MEDIA);
            pf.setFechaActualizacion(LocalDateTime.now());
            perfilesFinancierosRepository.save(pf);

            // C. TABLA: TRANSACCIONES (25 Registros - 5 por Usuario)
            crearTransaccion(u, c1, "Supermercado Plaza", 420.00, "GASTO", LocalDateTime.now().minusDays(2));
            crearTransaccion(u, c2, "Combustible Repsol", 300.00, "GASTO", LocalDateTime.now().minusDays(4));
            crearTransaccion(u, c3, "Streaming Netflix", 40.00, "GASTO", LocalDateTime.now().minusDays(6));
            crearTransaccion(u, c4, "Alquiler Departamento", 700.00, "GASTO", LocalDateTime.now().minusDays(10));
            crearTransaccion(u, c5, "Sueldo Mensual", 4500.00, "INGRESO", LocalDateTime.now().minusDays(15));

            // D. TABLA: PRESUPUESTOS (5 Registros - 1 por Usuario)
            Presupuesto p = new Presupuesto();
            p.setUsuario(u);
            p.setCategoria(c1);
            p.setLimite(new BigDecimal("1000.00"));
            p.setMes(7);
            p.setAnio(2026);
            presupuestoRepository.save(p);

            // E. TABLAS: ANALISIS_HISTORIAL & RECOMENDACIONES_HISTORIAL (5 Análisis + 10 Recomendaciones)
            AnalisisHistorial ah = new AnalisisHistorial();
            ah.setUsuario(u);
            ah.setIngresoMensual(new BigDecimal("4500.00"));
            ah.setNivelEndeudamiento(new BigDecimal("25.00"));
            ah.setFrecuenciaAhorro(FrecuenciaAhorro.MEDIA);
            ah.setPerfilResultado("En observación");
            ah.setProbabilidad(0.82);
            ah.setFechaAnalisis(LocalDateTime.now().minusDays(1));

            RecomendacionesHistorial r1 = new RecomendacionesHistorial();
            r1.setRecomendacionTexto("Monitorear los gastos recurrentes de entretenimiento");

            RecomendacionesHistorial r2 = new RecomendacionesHistorial();
            r2.setRecomendacionTexto("Aumentar la reserva financiera mensual");

            ah.agregarRecomendacion(r1);
            ah.agregarRecomendacion(r2);

            analisisHistorialRepository.save(ah);
        }

        System.out.println("✅ DataInitializer: ¡Las 7 tablas del esquema relacional han sido pobladas con 5 usuarios y datos de prueba completos!");
    }

    private void crearTransaccion(Usuario u, Categoria cat, String descripcion, Double monto, String tipo, LocalDateTime fecha) {
        Transaccion t = new Transaccion();
        t.setUsuario(u);
        t.setCategoria(cat);
        t.setDescripcion(descripcion);
        t.setMonto(monto);
        t.setTipo(tipo);
        t.setFecha(fecha);
        transaccionRepository.save(t);
    }
}
