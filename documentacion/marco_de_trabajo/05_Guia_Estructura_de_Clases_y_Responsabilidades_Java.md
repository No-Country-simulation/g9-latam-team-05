# 🏛️ Guía de Estructura de Clases, Controladores y Repositorios Java (Spring Boot)

Este documento define la **Guía Oficial de Límites de Dominio y Responsabilidades** para el equipo de Java Spring Boot. Su objetivo es mantener el código limpio, desacoplado y **evitar que se mezclen responsabilidades ajenas en controladores incorrectos** (por ejemplo, meter lógica de transacciones en `AuthController` o SQL en los Controllers).

---

## 🚫 REGLAS ANTI-CAOS Y LÍMITES DE RESPONSABILIDAD

> [!CAUTION]
> 1. **LOS CONTROLADORES (`@RestController`) NO CONTIENEN LÓGICA DE NEGOCIO:** Solo reciben HTTP, invocan al Service y retornan `ResponseEntity<DTO>`.
> 2. **PROHIBIDO SQL NATIVO EN CONTROLADORES Y SERVICIOS:** Todas las consultas a la base de datos se realizan exclusivamente a través de interfaces `@Repository` con Spring Data JPA.
> 3. **CADA CONTROLADOR TIENE UN ÚNICO DOMINIO RESTRINGIDO:** No agregues endpoints de transacciones en `AuthController`, ni endpoints de IA en `UsuarioController`.

---

## 📂 Mapa de  SSugerida  de Archivos y Responsabilidades

```
src/main/java/com/nocountry/fintech/
├── config/                     # Configuraciones de Seguridad JWT, CORS y Observabilidad
│   ├── SecurityConfig.java
│   ├── JwtFilter.java
│   └── ArchitectureObserver.java
│
├── controller/                 # CAPA DE CONTROLADORES REST (Solo Mapeo HTTP)
│   ├── AuthController.java     # 🔐 SOLO /api/auth/register y /api/auth/login
│   ├── DashboardController.java# 📊 SOLO /api/dashboard/resumen/{usuarioId}
│   ├── AnalisisController.java # ❤️ SOLO /api/analisis-financiero (Endpoint Corazón ML)
│   ├── TransaccionController.java# 💳 SOLO /api/transacciones/...
│   ├── PresupuestoController.java# 🎯 SOLO /api/presupuestos/...
│   └── CategoriaController.java# 🏷️ SOLO /api/categorias/...
│
├── service/                    # CAPA DE SERVICIOS (Lógica de Negocio y Cliente Python)
│   ├── AuthService.java        # Hash BCrypt, Generación JWT, Registro de Usuario
│   ├── DashboardService.java   # Aritmética KPI (Ingresos - Gastos)
│   ├── AnalisisIaService.java  # Patrón Híbrido, Invocación HTTP a Python y Snapshots BD
│   ├── TransaccionService.java # Auto-registro dinámico de categorías y CRUD transacciones
│   ├── PresupuestoService.java # Cálculo de presupuestos consumidos
│   └── PythonMlClient.java     # Cliente WebClient/RestTemplate hacia Python FastAPI
│
├── repository/                 # CAPA DE PERSISTENCIA (Spring Data JPA Agnóstico)
│   ├── UsuarioRepository.java  # findByEmail(String email)
│   ├── PerfilFinancieroRepository.java # findByUsuarioId(Long usuarioId)
│   ├── CategoriaRepository.java# findByNombre(String nombre)
│   ├── TransaccionRepository.java# findByUsuarioIdAndMes(...)
│   ├── PresupuestoRepository.java# findByUsuarioIdAndPeriodo(...)
│   ├── AnalisisHistorialRepository.java
│   └── RecomendacionHistorialRepository.java
│
└── dto/                        # DATA TRANSFER OBJECTS
    ├── request/                # DTOs de Entrada (Sin ID, validados con @Valid)
    └── response/               # DTOs de Salida (Con ID asignado por BD)
```

---

## 🎯 Desglose Detallado por Clase

---

### 1. 🔐 Mapeo del Dominio `Auth` (Autenticación)

*   **`AuthController.java` (`@RequestMapping("/api/auth")`):**
    *   `POST /api/auth/register`: Recibe `UsuarioRequestDto` ➔ Llama `AuthService.registrar()` ➔ Retorna `UsuarioResponseDto`.
    *   `POST /api/auth/login`: Recibe `LoginRequestDto` ➔ Llama `AuthService.login()` ➔ Retorna `LoginResponseDto` (JWT).
    *   ❌ **Prohibido:** No meter lógica de perfiles ni transacciones en este controller.
*   **`UsuarioRepository.java`:**
    ```java
    @Repository
    public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
        Optional<Usuario> findByEmail(String email);
        boolean existsByEmail(String email);
    }
    ```

---

### 2. 📊 Mapeo del Dominio `Dashboard`

*   **`DashboardController.java` (`@RequestMapping("/api/dashboard")`):**
    *   `GET /api/dashboard/resumen/{usuarioId}`: Llama `DashboardService.obtenerResumenKpi()` ➔ Retorna `ResumenKpiDto`.
*   **`DashboardService.java`:**
    *   Invoca `PerfilFinancieroRepository.findByUsuarioId()` y `TransaccionRepository.findByUsuarioIdAndTipo()`.
    *   Calcula `balanceNeto` y `tasaAhorro` sin SQL nativo.

---

### 3. ❤️ Mapeo del Dominio `Analisis` (El Corazón del Proyecto)

*   **`AnalisisController.java` (`@RequestMapping("/api/analisis-financiero")`):**
    *   `POST /api/analisis-financiero`: Recibe `AnalisisRequestDto` ➔ Llama `AnalisisIaService.procesarAnalisisFinanciero()` ➔ Retorna `AnalisisResponseDto`.
*   **`AnalisisIaService.java`:**
    *   Implementa el **Patrón Híbrido**: Si la petición trae `transacciones`, las usa (Modo Jueces); si no, consulta `TransaccionRepository` (Modo Producción BD).
    *   Invoca a `PythonMlClient` via HTTP `POST http://localhost:8000/api/v1/predict-health`.
    *   Persiste el Snapshot en `AnalisisHistorialRepository` y `RecomendacionHistorialRepository`.

---

### 4. 💳 Mapeo del Dominio `Transaccion`

*   **`TransaccionController.java` (`@RequestMapping("/api/transacciones")`):**
    *   `GET /api/transacciones/usuario/{usuarioId}/distribucion`: Devuelve los porcentajes por categoría.
    *   `GET /api/transacciones/usuario/{usuarioId}/recientes`: Devuelve las últimas 5 transacciones.
    *   `POST /api/transacciones`: Registra una nueva transacción.
*   **`TransaccionRepository.java`:**
    ```java
    @Repository
    public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
        List<Transaccion> findByUsuarioId(Long usuarioId);
        List<Transaccion> findByUsuarioIdAndTipo(Long usuarioId, String tipo);
        List<Transaccion> findTop5ByUsuarioIdOrderByFechaDesc(Long usuarioId);
    }
    ```

---

### 5. 🏷️ Mapeo del Dominio `Categoria`

*   **`CategoriaController.java` (`@RequestMapping("/api/categorias")`):**
    *   `GET /api/categorias`: Retorna el catálogo maestro de categorías (`id`, `nombre`, `tipo`, `icono`, `color`).
*   **`CategoriaRepository.java`:**
    ```java
    @Repository
    public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
        Optional<Categoria> findByNombre(String nombre);
        boolean existsByNombre(String nombre);
    }
    ```
