# ☕ FinanceAI — Backend REST API Module (Java / Spring Boot)

> **NoCountry / Alura ONE — Team 05 (G9)**  
> Módulo Backend de orquestación transaccional, seguridad stateless e integración con microservicio de Inteligencia Artificial para la plataforma **FinanceAI**.

---

## 📌 Visión General

El backend de **FinanceAI** actúa como el motor central y gateway orquestador del sistema. Es responsable del procesamiento seguro de autenticación (JWT), la persistencia del perfil financiero y transaccional del usuario, el cálculo en tiempo real de Métricas e Indicadores Clave de Desempeño (KPIs) y la integración sincrónica con el **Microservicio de IA en Python (FastAPI)** para generar diagnósticos y recomendaciones financieras adaptativas.

Diseñado bajo la **Arquitectura en Capas (Layered Architecture)** y patrones RESTful empresariales, garantiza alto rendimiento, separación de responsabilidades y bajo acoplamiento mediante el uso riguroso de **DTOs (Data Transfer Objects)**.

---

## 🏗️ Arquitectura y Flujo de Integración

El sistema sigue una arquitectura distribuida donde el backend Spring Boot orquesta la lógica transaccional y la persistencia relacional, comunicándose con la IA para extender las capacidades analíticas:

```bash
┌─────────────────────────────────────────────────────────────────────────┐
│                        CLIENT (Frontend Angular)                        │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │
                                     │ HTTP REST + Bearer JWT
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       SPRING BOOT BACKEND (Java 21)                      │
│                                                                          │
│  [Security Layer]       ➜  JwtAuthenticationFilter + BCrypt             │
│  [Controller Layer]     ➜  Analisis, Auth, Dashboard, Transacciones     │
│  [DTO Layer]            ➜  Inmutabilidad de datos y validaciones        │
│  [Service Layer]        ➜  Lógica de negocio y orquestación             │
│  [Repository Layer]     ➜  Spring Data JPA                              │
└──────────────┬──────────────────────────────────────────┬────────────────┘
               │                                          │
               │ JDBC / ORM                               │ RestClient (HTTP)
               ▼                                          ▼
┌──────────────────────────────┐          ┌──────────────────────────────────┐
│ Oracle Autonomous DB (OCI)   │          │ AI MICROSERVICE (Python/FastAPI) │
│ / PostgreSQL (Dev Local)     │          │  - Clasificación de perfil       │
│                              │          │  - Recomendaciones LLM           │
└──────────────────────────────┘          └──────────────────────────────────┘

```
### 🔁 Flujo de Inteligencia Financiera (Spring Boot ↔ Python FastAPI):
1. **Petición del Usuario**: El cliente solicita un diagnóstico financiero a través de los endpoints `/api/analisis-financiero`.
2. **Consolidación de Métricas**: `AnalisisPerfilService` compila el perfil financiero del usuario (ingresos, gastos fijos, deudas, ahorros, metas) y consulta la base de datos relacional.
3. **Invocación al Microservicio de IA**: Mediante `ConsumoFastApi` (utilizando `RestClient` de Spring Boot 3), se emite una petición HTTP POST estructurada hacia la API en Python (`/api/v1/predict-health` o `/api/v1/classify-transactions`).
4. **Persistencia e Histórico**: La recomendación devuelta por la IA se almacena en el repositorio (`AnalisisHistorialRepository` / `RecomendacionesHistorialRepository`) para auditoría e historial del usuario, y se envía formateada al Frontend en un `DTO`.

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología | Descripción |
| :--- | :--- | :--- |
| **Lenguaje** | Java 21 LTS | Estándar moderno y optimizado de desarrollo empresarial. |
| **Framework** | Spring Boot 3.x | Framework base para microservicios y REST APIs. |
| **Seguridad** | Spring Security + Auth0 JWT | Autenticación Stateless y encriptación de contraseñas con BCrypt. |
| **Persistencia** | Spring Data JPA / Hibernate | Mapeo Objeto-Relacional (ORM) y abstracción de consultas. |
| **Base de Datos** | Oracle Autonomous DB / PostgreSQL | Persistencia relacional en la nube (OCI) y entorno local. |
| **Cliente HTTP** | Spring RestClient | Integración sincrónica con el Microservicio de IA en Python. |
| **Herramientas** | Lombok / Maven | Reducción de código repetitivo y gestión de dependencias. |

---

## 🗄️ Estrategia de Persistencia y Base de Datos

La aplicación utiliza **Spring Data JPA / Hibernate** como capa de abstracción relacional (ORM). Esto permite mantener una arquitectura **agnóstica a la base de datos**, facilitando la flexibilidad entre el entorno de desarrollo y producción:

* **Entorno de Desarrollo (Local):** PostgreSQL 15+.
* **Entorno de Producción (Deploy / Cloud):** **Oracle Autonomous Database** alojada en **Oracle Cloud Infrastructure (OCI)**.
* **Estrategia DDL (`ddl-auto=update`):** Hibernate analiza las entidades JPA mapeadas (`@Entity`) en el paquete `model` y autogenera/actualiza automáticamente el esquema relacional en la base de datos a partir del modelo del sistema, el cual fue diseñado bajo las reglas de la **Tercera Forma Normal (3NF)** para evitar redundancia e inconsistencia de datos.

> ⚠️ **Nota de Seguridad y Producción:**  
> Aunque `ddl-auto=update` facilita el prototipado rápido, para un entorno de **Producción Crítico / Enterprise** es altamente aconsejable deshabilitar el autogenerado (`ddl-auto=validate` o `none`) y gestionar el esquema mediante **scripts SQL controlados de migración (Flyway o Liquibase)**. Esto previene alteración accidental de tablas, bloqueos en caliente o pérdida inadvertida de datos.

---

## 🔌 Endpoints Principales de la API (`/api`)

### 🔐 Autenticación y Usuarios (`/api/auth`, `/api/usuarios`)
* `POST /api/auth/register`: Registra nuevos usuarios en el sistema.
* `POST /api/auth/login`: Autentica credenciales y retorna el token JWT.
* `GET /api/usuarios`: Lista todos los usuarios registrados.
* `GET /api/usuarios/{id}`: Obtiene el detalle de un usuario por ID.

### 📊 Dashboard & KPIs (`/api/dashboard`)
* `GET /api/dashboard/resumen/{id}`: Retorna el resumen consolidado de KPIs (ingresos, gastos, balances) para el usuario indicado.

### 💳 Transacciones (`/api/transacciones`)
* `GET /api/transacciones/usuario/{usuarioId}/recientes`: Retorna las transacciones paginadas más recientes del usuario.
* `POST /api/transacciones/registrar`: Registra una transacción (Ingreso/Egreso). Si es `EGRESO`, invoca automáticamente al microservicio de IA para inferir su categoría a partir de la descripción.
* `GET /api/transacciones/usuario/{usuarioId}/distribucion`: Retorna la distribución porcentual de gastos/ingresos del usuario.
* `DELETE /api/transacciones/{id}`: Elimina una transacción específica.

### 📈 Perfil Financiero (`/api/perfiles-financieros`)
* `GET /api/perfiles-financieros/usuario/{usuarioId}`: Obtiene la información financiera del usuario.
* `POST /api/perfiles-financieros`: Registra el perfil financiero inicial del usuario autenticado.
* `PUT /api/perfiles-financieros`: Actualiza las métricas y montos del perfil financiero.

### 🤖 Inteligencia Artificial y Análisis (`/api/analisis-financiero`)
* `POST /api/analisis-financiero`: Procesa la matriz financiera mediante el microservicio en FastAPI y genera el diagnóstico/recomendación.
* `GET /api/analisis-financiero/historial`: Obtiene el historial guardado de diagnósticos del usuario autenticado.

### 🏷️ Categorías y Presupuestos (`/api/categorias`, `/api/presupuestos`)
* `GET /api/categorias`: Lista todas las categorías registradas en el sistema.
* `GET /api/categorias/tipo/{tipo}`: Filtra categorías por tipo (`INGRESO` / `EGRESO`).
* `GET /api/presupuestos/usuario/{usuarioId}`: Obtiene los presupuestos asignados por el usuario.
* `POST /api/presupuestos`: Crea la planificación de presupuesto para una categoría.

---

## 🚀 Configuración y Ejecución Local

### Prerrequisitos
* **Java Development Kit (JDK):** Java 21 LTS.
* **Base de Datos Local:** PostgreSQL (o instancia configurada de Oracle Cloud).
* **Apache Maven:** 3.8+

### Pasos de Instalación

1. **Clonar el repositorio y acceder a la carpeta del proyecto**:
   ```bash
   git clone https://github.com/No-Country-simulation/g9-latam-team-05.git
   cd FinanceAI
   ```

2. **Configurar el perfil de Base de Datos**:
   Ajusta las propiedades en application.properties (o application-postgres.properties) según tu entorno local:

   ### Ejemplo Desarrollo Local (PostgreSQL)
   ```bash
   spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
   spring.datasource.username=postgres
   spring.datasource.password=tu_password
   spring.jpa.hibernate.ddl-auto=update
   ```
   ### En Producción, se conecta a Oracle Autonomous Database mediante OCI JDBC Driver.

3. **Compilar y Levantar la Aplicación**:
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```
   **La API iniciará en http://localhost:8080 y autogenerará las tablas en la base de datos configurada.**

---

## 🧪 Calidad de Código y Patrones de Diseño

* **Aislamiento DTO**: Cero exposición directa de Entidades JPA en la capa Controller; prevención total de ciclos de serialización JSON.
* **Seguridad Stateless & CORS**: Autenticación Bearer Token controlada mediante `JwtAuthenticationFilter` y politicas CORS abiertas a integración multi-origen.
* **Manejo Global de Excepciones**: Centralizado vía `@RestControllerAdvice` (`GlobalExceptionHandler`), capturando errores 404 (`ResourceNotFoundException`), validaciones de campos (`MethodArgumentNotValidException`), reglas de negocio 400 (`IllegalArgumentException`) y fallos no controlados 500.
* **Manejo Resiliente de IA**: Integración HTTP orientada a microservicios desacoplados vía `ConsumoFastApi`, soportada con variables de entorno parametrizadas (`PYTHON_FASTAPI_URL`).
* **Inferencia Transaccional Automática**: Integración sincrónica con NLP para predecir y asignar categorías a los egresos según la descripción ingresada por el usuario.
