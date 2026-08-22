# 🅰️ Finance AI - Frontend (Angular)

¡Bienvenido al módulo frontend de **Finance AI**! Esta es una aplicación de última generación estructurada con **Angular v17+** (componentes Standalone y gestión de estado mediante **Signals**), diseñada para ofrecer una interfaz oscura y cristalizada (glassmorphism) e interactiva.

Este documento tiene como fin guiar a todo el equipo de desarrollo para levantar el proyecto localmente sin complicaciones.

---

## 🚀 Guía de Configuración y Ejecución Rápida

Sigue estos sencillos pasos desde la terminal de tu sistema para levantar el prototipo:

### Paso 1: Navegar al directorio del frontend
Abre tu terminal (PowerShell, Git Bash o Símbolo del Sistema) en la raíz del proyecto y muévete a la carpeta `frontend`:
```powershell
cd g9-latam-team-05/frontend
```

### Paso 2: Instalar las dependencias del proyecto
Instala las librerías necesarias del proyecto (incluye Angular, Chart.js para visualización de gráficos y configuraciones base):
```powershell
npm install
```

### Paso 3: Iniciar el servidor de desarrollo local
Ejecuta el siguiente comando para levantar el servidor web local:
```powershell
npm start
```
*(También puedes usar `ng serve`, pero `npm start` corre el script optimizado configurado en el package.json).*

### Paso 4: Abrir la aplicación en el navegador
Una vez que en la consola diga `Application bundle generation complete`, abre tu navegador e ingresa a:
👉 **[http://localhost:4200](http://localhost:4200)**

---

## 🏛️ Estructura del Código del Frontend (Modular por Características)

Para mantener la base del código limpia y evitar que la arquitectura se vuelva innecesariamente compleja, el código sigue la guía de Angular Moderno:

```text
src/app/
├── core/                         # Capa de datos compartida y servicios globales
│   └── services/
│       └── finance.ts            # SERVICIO PRINCIPAL: Contiene el estado reactivo (Signals)
├── shared/                       # Componentes visuales genéricos
│   └── components/               # kpi-card, csv-upload, profile-badge, etc.
├── features/                     # Páginas/Pantallas individuales (Lazy loaded)
│   ├── auth/                     # Login e inicio de sesión
│   ├── onboarding/               # Pasos de calibración financiera inicial
│   ├── dashboard/                # Métricas principales y gráficos circulares
│   ├── transactions/             # Tabla de gastos e importación de CSV
│   ├── simulator/                # Proyección dinámica de gastos e ingresos
│   ├── profile/                  # Diagnóstico de perfil (Saludable/En Riesgo)
│   ├── recommendations/            # Tarjetas de sugerencias del motor de IA
│   └── history/                  # Evolución histórica temporal de gastos
└── shell/                        # Componente de Layout (Barra de navegación lateral)
```

---

## 💡 ¿Cómo funciona la Reactividad? (Para Desarrolladores)

El sistema **no utiliza NGRX ni librerías pesadas de estado**. En su lugar, utiliza **Angular Signals** en `src/app/core/services/finance.ts`:

1.  **Estado Global Centralizado:** El ingreso mensual, deuda, transacciones y recomendaciones son Signals reactivos (`signal()`).
2.  **Cálculos Automáticos:** El perfil de riesgo, la tasa de ahorro y los gráficos se recalculan en tiempo real usando selectores derivados (`computed()`).
3.  **Fácil Reemplazo por API Real:** Cuando el backend esté listo, las promesas y retardos simulados en `FinanceService` se podrán reemplazar directamente por peticiones `HttpClient.post` o `HttpClient.get` hacia los endpoints del backend en Java Spring Boot sin alterar el código de las pantallas..