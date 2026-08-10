# Arquitectura del Backend (Spring Boot 3 + Java 21)

Documentación oficial resumida de la arquitectura, estándares de diseño y catálogo de controllers/endpoints.

---

## 🏛️ Principios de Arquitectura & Clean Code

1. **Controllers Ultra-Ligeros (Single Responsibility Principle - SRP):**
   * Los controllers actúan exclusivamente como adaptadores HTTP de entrada.
   * **Sin lógica de negocio:** No contienen validaciones de negocio ni transformaciones pesadas; delegan directamente a la capa de Servicio.
   * **Ejemplo Estándar:**
     ```java
     @GetMapping("/usuario/{usuarioId}/periodo")
     public ResponseEntity<List<PresupuestoResponseDto>> obtenerPorPeriodo(
             @PathVariable Long usuarioId,
             @RequestParam Integer anio,
             @RequestParam Integer mes) {
         return ResponseEntity.ok(presupuestoService.listarPorUsuarioYPeriodo(usuarioId, anio, mes));
     }
     ```

2. **Seguridad y Extracción de Contexto JWT:**
   * Las peticiones autenticadas (`POST`, `PUT`, `DELETE`) no reciben el `userId` en el cuerpo del JSON.
   * Spring Security resuelve automáticamente la entidad `Usuario` mediante `Authentication.getName()` extrayendo el email del token `Authorization: Bearer <TOKEN>`.

3. **Capa de Integración de IA (Spring Boot ➔ FastAPI Python):**
   * `ConsumoFastApi.java` y `AnalisisPerfilService.java` consumen los microservicios de ML/NLP en Python (`http://localhost:8000`).
   * **Modo Resiliente:** Si el microservicio de Python está fuera de línea, activa la contingencia local manteniendo la app operativa.

---

## 🛰️ Catálogo Resumido de Controllers y Endpoints

### 1. 🔑 Autenticación (`/api/auth`)
* `POST /api/auth/register` ➔ Registro de nuevo usuario.
* `POST /api/auth/login` ➔ Autenticación y generación de JWT Token.

### 2. 📊 Dashboard Consolidado (`/api/dashboard`)
* `GET /api/dashboard/resumen/{usuarioId}` ➔ Retorna KPI consolidado (Ingresos, Gastos, Balance, Tasa Ahorro).

### 3. 💳 Transacciones (`/api/transacciones`)
* `GET /api/transacciones/usuario/{usuarioId}/recientes` ➔ Historial de movimientos recientes.
* `POST /api/transacciones/registrar` ➔ Registrar nuevo ingreso/gasto (asociado automáticamente al usuario del Token JWT).
* `GET /api/transacciones/usuario/{usuarioId}/distribucion` ➔ Distribución de gastos por categorías clasificadas con IA NLP.
* `DELETE /api/transacciones/{id}` ➔ Eliminación de transacción.

### 4. 👤 Perfil Financiero (`/api/perfiles-financieros`)
* `GET /api/perfiles-financieros/usuario/{usuarioId}` ➔ Obtiene datos declarados del perfil financiero.
* `POST /api/perfiles-financieros` ➔ Creación inicial de perfil financiero.
* `PUT /api/perfiles-financieros` ➔ Actualización de perfil financiero.

### 5. 🤖 Diagnóstico e Inferencia IA (`/api/analisis-financiero`)
* `POST /api/analisis-financiero` ➔ Ejecuta el análisis predictivo de salud financiera (Scikit-Learn).
* `GET /api/analisis-financiero/historial` *(Pendiente)* ➔ Retorna la secuencia histórica de análisis guardados en `ANALISIS_HISTORIAL`.

### 6. 💰 Presupuestos (`/api/presupuestos`)
* `GET /api/presupuestos/usuario/{usuarioId}/periodo` ➔ Consulta presupuestos por mes y año.

---

## 🗄️ Esquema de Persistencia Principal
* `USUARIOS` ➔ Cuentas de usuario y hashes BCrypt.
* `TRANSACCIONES` ➔ Movimientos financieros vinculados a `user_id`.
* `PERFILES_FINANCIEROS` ➔ Parámetros de ingreso, endeudamiento y frecuencia de ahorro.
* `ANALISIS_HISTORIAL` ➔ Auditoría e historial temporal de diagnósticos de IA.
