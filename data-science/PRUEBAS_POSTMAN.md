# 🧪 Guía de Pruebas Unitarias en Postman (Python FastAPI Mock Service)

Este archivo contiene la guía detallada de configuración y las aserciones de pruebas automatizadas (Tests) para validar los endpoints del microservicio de Python FastAPI.

---

## 🚀 Preparación
1. Asegúrate de tener corriendo el servicio local en el puerto `8000` (`python main.py` o `uvicorn main:app --reload --port 8000`).
2. Abre **Postman**.

---

## 📡 Endpoint 1: Clasificación NLP en Lote (`POST /api/v1/classify-transactions`)

*   **Método:** `POST`
*   **URL:** `http://localhost:8000/api/v1/classify-transactions`
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw JSON):**
    ```json
    {
      "transacciones": [
        { "id": 1, "text": "Supermercado Plaza", "monto": 420.00 },
        { "id": 2, "text": "Gasolinera Repsol", "monto": 300.00 }
      ]
    }
    ```

*   **Resultado Esperado (JSON Response - 200 OK):**
    ```json
    {
      "clasificados": [
        {
          "id": 1,
          "categoriaPredicha": "Alimentación",
          "monto": 420.00
        },
        {
          "id": 2,
          "categoriaPredicha": "Transporte",
          "monto": 300.00
        }
      ]
    }
    ```

---

## 📡 Endpoint 2: Predictor de Salud Financiera (`POST /api/v1/predict-health`)

*   **Método:** `POST`
*   **URL:** `http://localhost:8000/api/v1/predict-health`
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw JSON):**
    ```json
    {
      "ingreso_mensual": 4500.00,
      "nivel_endeudamiento": 40.00,
      "frecuencia_ahorro": "Media",
      "transacciones": [
        { "descripcion": "Supermercado", "valor": 420.00 },
        { "descripcion": "Combustible", "valor": 300.00 },
        { "descripcion": "Streaming", "valor": 40.00 }
      ]
    }
    ```

*   **Resultado Esperado (JSON Response - 200 OK):**
    ```json
    {
      "perfil_financiero": "En observación",
      "probabilidad": 0.82,
      "resumen_gastos": {
        "alimentacion": 420.00,
        "transporte": 300.00,
        "ocio": 40.00
      },
      "recomendaciones": [
        "Monitorear de cerca tus gastos recurrentes y suscripciones.",
        "Intenta recortar gastos pequeños para aumentar tu tasa de ahorro por encima del 15%.",
        "Establece un fondo de emergencias equivalente a 3 meses de gastos básicos."
      ]
    }
    ```
