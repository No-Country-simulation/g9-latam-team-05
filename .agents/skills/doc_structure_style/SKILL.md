---
name: doc_structure_style
description: Enforces the strict trilateral documentation style, JWT security patterns, deferred classification rules, and approval workflow.
---
# 📐 Reglas de Oro para Documentación y Desarrollo Trilateral

Siempre que escribas o actualices especificaciones técnicas, contratos de APIs o arquitectura en este proyecto, debes seguir estrictamente estas reglas:

## 1. 🛑 Validación y Alineación Explícita Previa
- **NO MODIFICAR CÓDIGO NI CONTRATOS A CIEGAS:** Nunca realices cambios en clases Java, componentes Angular o archivos de contrato (`02_Contrato_Oficial_de_APIs_Trilateral.md`) sin que el usuario haya revisado y aprobado explícitamente la propuesta en el análisis previo.

## 2. 🔐 Seguridad JWT (Cero IDs de Usuario en Payloads)
- En todas las peticiones authenticated (que usan cabeceras `Authorization: Bearer <TOKEN>`), **NUNCA incluir `usuarioId` o `userId` en el JSON de Request Body**.
- Java debe extraer el usuario autenticado a través del contexto de seguridad JWT (`SecurityContextHolder`).

## 3. ⏱️ Lógica de Clasificación Diferida de Transacciones
- El endpoint de registro (`POST /api/transacciones/registrar`) y el de lote CSV persisten **todas** las transacciones inicialmente con `categoria_id = NULL` (tanto gastos como ingresos).
- La resolución de categorías (asignar `"Ingresos"` o clasificar gastos vía NLP en Python) se difiere y ejecuta síncronamente al cargar la pantalla de Análisis Financiero (`POST /api/analisis-financiero`).

## 4. 📐 Patrón de Estructura Trilateral en Documentación
Para documentar cada pantalla, nivel o módulo, utiliza siempre esta estructura de 4 pasos:

1. **🎨 Elementos UI que Pinta el Frontend**
   - Desglose de componentes visuales, controles y botones.

2. **🔄 Flujo de Datos (Desglose Trilateral):**
   - **1. Frontend envía a Java Backend:** Método HTTP, URL de Endpoint, y JSON de Request Body (sin usuarioId).
   - **2. Java procesa y persiste:** Lógica en base de datos PostgreSQL/Oracle y resolución JWT.
   - **3. Microservicio Python (IA / ML):** Endpoints de FastAPI invocados, payload a la IA y respuesta. Poner `N/A` si no aplica en ese paso.
   - **4. Java responde al Frontend:** Código de estado HTTP y JSON de Response Body.
