# 🚀 Plan de Trabajo Exclusivo: SEMANA 2 (Especificación Dividida por Roles)

Este documento está estructurado de forma **100% independiente por rol**. Cada desarrollador (Frontend, Backend Java o Backend Python) puede leer exclusivamente su sección para conocer sus tareas de la **Semana 2 / Fase 2** y los formatos de datos JSON que debe enviar y recibir.

---

## 📘 ¿Cómo se llama esta práctica en la Industria del Software?

En las grandes empresas de tecnología (Google, Amazon, bancos y startups internacionales), esto que estás haciendo se conoce formalmente como:

1. **API-First Development (Desarrollo Orientado a la API Primero):**
   Es la metodología estándar donde la interfaz de comunicación (los contratos JSON) se diseña y congela **ANTES** de escribir la primera línea de código en Angular, Java o Python. Esto evita que los desarrolladores adivinen nombres de variables o tengan que rehacer código.
2. **Contract-Driven Development (Desarrollo Guiado por Contratos):**
   El contrato JSON actúa como la **"Única Fuente de la Verdad" (Single Source of Truth - SSOT)**. Si el contrato dice que el campo se llama `ingresoMensual`, ningún rol puede cambiarlo a `ingreso_mensual` sin previa aprobación.
3. **Matriz de Integración de Servicios (Service Integration Matrix):**
   Es la tabla maestra unificada que mapea el flujo de peticiones entre el cliente web (Frontend), el orquestador principal (Backend Java) y los microservicios especializados (Backend Python IA).

---

## 🗺️ Matriz Maestra Unificada de Endpoints (Semana 2)

| # | Módulo | Endpoint (Ruta REST) | Método | Origen (Emisor) | Destino (Receptor) | Propósito del Endpoint | Base de Datos / Procesamiento |
| :---: | :--- | :--- | :---: | :---: | :---: | :--- | :--- |
| **1** | **Auth** | `/api/auth/register` | `POST` | Frontend | Java Backend | Registrar nuevo usuario | Inserta en `usuarios` (Contraseña BCrypt) |
| **2** | **Auth** | `/api/auth/login` | `POST` | Frontend | Java Backend | Iniciar sesión y obtener token JWT | Consulta en `usuarios` + Genera Token JWT |
| **3** | **Perfil** | `/api/perfiles-financieros` | `PUT` | Frontend | Java Backend | Crear/Actualizar contexto económico | Upsert en `perfiles_financieros` |
| **4** | **Perfil** | `/api/perfiles-financieros/{userId}` | `GET` | Frontend | Java Backend | Consultar perfil financiero | SELECT en `perfiles_financieros` |
| **5** | **Transacción**| `/api/transacciones` | `POST` | Frontend | Java Backend | Registrar nuevo ingreso o egreso | Inserta en `transacciones` |
| **6** | **Transacción**| `/api/transacciones/usuario/{userId}` | `GET` | Frontend | Java Backend | Historial de transacciones del usuario | SELECT JOIN `transacciones` + `categorias` |
| **7** | **Presupuesto**| `/api/presupuestos` | `POST` | Frontend | Java Backend | Fijar límite de gasto por categoría | Inserta en `presupuestos` |
| **8** | **Presupuesto**| `/api/presupuestos/usuario/{userId}` | `GET` | Frontend | Java Backend | Consultar límites y consumos | SELECT JOIN `presupuestos` vs `transacciones` |
| **9** | **IA Diagnóstico**| `/api/analisis/calcular` | `POST` | Frontend | Java Backend | Disparar análisis de Salud Financiera | Orquesta petición externa + Guarda Snapshot DB |
| **10**| **IA Microservicio**| `/api/v1/predict-health` | `POST` | Java Backend | Python FastAPI | Inferencia de modelo ML (FastAPI) | Procesa modelo `.joblib` en memoria |

---

## 💻 ROL 1: FRONTEND (Angular / Cliente Web)

### 📋 Tareas de la Semana 2
1. **Layout Shell:** Navegación base, Sidebar, Topbar.
2. **Pantallas de Autenticación:** Formularios reactivos con validaciones visuales para Login (`/login`) y Registro (`/register`).
3. **Onboarding Stepper:** Formulario guiado de 3 pasos para ingresar Perfil Financiero (`/onboarding`).
4. **Dashboard & Transacciones:** Tabla de movimientos y gráficos de consumo por categoría usando `Chart.js`.
5. **Servicios HTTP (`HttpClient`):** Conectar los componentes con las peticiones REST a Java usando los JSONs detallados a continuación.

### 📡 JSONs del FRONTEND (Envía y Recibe)

#### A. Login de Usuario (`POST /api/auth/login`)
*   **Frontend Envía a Java (Request):**
    ```json
    {
      "email": "usuario@fintech.com",
      "password": "Password123!"
    }
    ```
*   **Frontend Recibe de Java (Response 200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer",
      "usuario": {
        "id": 1,
        "nombre": "Carlos Mendoza",
        "email": "usuario@fintech.com"
      }
    }
    ```

#### B. Perfil Financiero / Onboarding (`PUT /api/perfiles-financieros`)
*   **Frontend Envía a Java (Request):**
    ```json
    {
      "userId": 1,
      "ingresoMensual": 3500.00,
      "nivelEndeudamiento": 25.50,
      "frecuenciaAhorro": "MEDIA"
    }
    ```
*   **Frontend Recibe de Java (Response 200 OK):**
    ```json
    {
      "userId": 1,
      "ingresoMensual": 3500.00,
      "nivelEndeudamiento": 25.50,
      "frecuenciaAhorro": "MEDIA",
      "fechaActualizacion": "2026-07-26T23:25:00"
    }
    ```

#### C. Registrar Transacción (`POST /api/transacciones`)
*   **Frontend Envía a Java (Request):**
    ```json
    {
      "userId": 1,
      "categoriaId": 2,
      "descripcion": "Supermercado Wong - Compra quincenal",
      "monto": 420.50,
      "tipo": "GASTO",
      "fecha": "2026-07-26T18:30:00"
    }
    ```
*   **Frontend Recibe de Java (Response 201 Created):**
    ```json
    {
      "id": 15,
      "userId": 1,
      "categoria": {
        "id": 2,
        "nombre": "Alimentación",
        "icono": "shopping-cart",
        "color": "#FF5733"
      },
      "descripcion": "Supermercado Wong - Compra quincenal",
      "monto": 420.50,
      "tipo": "GASTO",
      "fecha": "2026-07-26T18:30:00"
    }
    ```

#### D. Diagnóstico de Salud Financiera IA (`POST /api/analisis/calcular`)
*   **Frontend Envía a Java (Request):**
    ```json
    {
      "userId": 1
    }
    ```
*   **Frontend Recibe de Java (Response 200 OK):**
    ```json
    {
      "analisisId": 8,
      "userId": 1,
      "perfilResultado": "Saludable",
      "probabilidad": 0.885,
      "ingresoMensual": 3500.00,
      "nivelEndeudamiento": 25.50,
      "frecuenciaAhorro": "MEDIA",
      "fechaAnalisis": "2026-07-26T23:25:00",
      "recomendaciones": [
        "Mantener el nivel de endeudamiento por debajo del 30% de tus ingresos.",
        "Destinar un 15% adicional del saldo libre al fondo de emergencia mensual."
      ]
    }
    ```

---

## ☕ ROL 2: BACKEND JAVA (Spring Boot / Orquestador REST)

### 📋 Tareas de la Semana 2
1. **`AuthController`:** Exponer endpoints `/api/auth/register` y `/api/auth/login`.
2. **`PerfilFinancieroController`:** Exponer `PUT /api/perfiles-financieros` guardando en la tabla `perfiles_financieros`.
3. **`TransaccionController`:** Exponer `POST /api/transacciones` y `GET /api/transacciones/usuario/{id}`.
4. **`PresupuestoController`:** Exponer `POST /api/presupuestos`.
5. **`AnalisisController`:** Exponer `POST /api/analisis/calcular`. Consulta datos del usuario, los envía al Microservicio Python mediante `RestTemplate`/`WebClient`, guarda el Snapshot en PostgreSQL (`analisis_historial` + `recomendaciones_historial`) y responde al Frontend.

### 📡 JSONs del BACKEND JAVA (Recepción y Envío Interno)

#### A. Recepción desde el Frontend (`POST /api/transacciones`)
*   **Java Recibe del Frontend (Request):**
    ```json
    {
      "userId": 1,
      "categoriaId": 2,
      "descripcion": "Supermercado Wong",
      "monto": 420.50,
      "tipo": "GASTO",
      "fecha": "2026-07-26T18:30:00"
    }
    ```
*   **Java Responde al Frontend (Response 201 Created):**
    ```json
    {
      "id": 15,
      "userId": 1,
      "categoria": { "id": 2, "nombre": "Alimentación" },
      "descripcion": "Supermercado Wong",
      "monto": 420.50,
      "tipo": "GASTO",
      "fecha": "2026-07-26T18:30:00"
    }
    ```

#### B. Comunicación Interna Java ➔ Python FastAPI (`POST /api/v1/predict-health`)
*   **Java Envía a Python Microservice:**
    ```json
    {
      "user_id": 1,
      "ingreso_mensual": 3500.00,
      "nivel_endeudamiento": 25.50,
      "frecuencia_ahorro": "MEDIA",
      "total_gastos_mes": 1850.00,
      "transacciones_recientes_count": 24
    }
    ```
*   **Java Recibe de Python Microservice (200 OK):**
    ```json
    {
      "user_id": 1,
      "perfil_resultado": "Saludable",
      "probabilidad": 0.885,
      "recomendaciones": [
        "Mantener el nivel de endeudamiento por debajo del 30% de tus ingresos.",
        "Destinar un 15% adicional del saldo libre al fondo de emergencia mensual."
      ]
    }
    ```

---

## 🐍 ROL 3: BACKEND PYTHON (FastAPI / IA / Data Science)

### 📋 Tareas de la Semana 2
1. **Limpieza del Dataset:** Normalizar las variables financieras (`ingreso_mensual`, `nivel_endeudamiento`, `frecuencia_ahorro`).
2. **Entrenamiento del Modelo ML:** Entrenar el clasificador con Scikit-Learn para predecir el diagnóstico (`Saludable`, `En observación`, `En riesgo`) y calcular la probabilidad.
3. **Serialización:** Exportar el modelo entrenado como ejecutable binario `modelo_salud_financiera.joblib`.
4. **Servidor FastAPI:** Exponer el endpoint `POST /api/v1/predict-health` en puerto `8000` sirviendo la inferencia del modelo.

### 📡 JSONs del BACKEND PYTHON (Entrada y Salida Microservicio ML)

#### Endpoint Predicción de IA (`POST /api/v1/predict-health`)
*   **Python Recibe de Java Backend (Request Body):**
    ```json
    {
      "user_id": 1,
      "ingreso_mensual": 3500.00,
      "nivel_endeudamiento": 25.50,
      "frecuencia_ahorro": "MEDIA",
      "total_gastos_mes": 1850.00,
      "transacciones_recientes_count": 24
    }
    ```
*   **Python Devuelve a Java Backend (Response Body - 200 OK):**
    ```json
    {
      "user_id": 1,
      "perfil_resultado": "Saludable",
      "probabilidad": 0.885,
      "recomendaciones": [
        "Mantener el nivel de endeudamiento por debajo del 30% de tus ingresos.",
        "Destinar un 15% adicional del saldo libre al fondo de emergencia mensual."
      ]
    }
    ```

---

## 🔮 Visión General de las Próximas Semanas (Semanas 3 a 6)

> **Resumen General del Flujo Futuro:** Una vez concluidos los desarrollos independientes de la Semana 2, la **Semana 3** se destinará a las pruebas unitarias y acoplamiento total de Angular con Java. La **Semana 4** añadirá el cálculo de alertas del 80% y 100% de presupuestos. La **Semana 5** ejecutará la migración a la nube de Oracle (OCI Compute, Autonomous DB y Object Storage), culminando en la **Semana 6** con la estabilización, pruebas de carga y la demostración final del proyecto.
