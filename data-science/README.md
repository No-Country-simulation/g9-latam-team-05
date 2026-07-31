# 🐍 Finance AI - Data Science (FastAPI Mock Service)

Este es el microservicio en Python FastAPI desarrollado para simular las funcionalidades del modelo de Inteligencia Artificial (NLP y Predictor de Salud Financiera) requerido por el backend orquestador de Java.

## 🛠️ Requisitos e Instalación (Ejecutar en terminal CMD)

1. **Abrir la terminal CMD (Símbolo del Sistema) e ir a la carpeta del proyecto:**
   ```cmd
   cd C:\Users\Edison\Downloads\Proyectos\Oracle\g9-latam-team-05\data-science
   ```

2. **Crear el entorno virtual de Python:**
   ```cmd
   python -m venv venv
   ```

3. **Activar el entorno virtual:**
   ```cmd
   venv\Scripts\activate
   ```

4. **Instalar dependencias:**
   ```cmd
   pip install -r requirements.txt
   ```

## 🚀 Ejecutar el Servicio (Ejecutar en terminal CMD)

Para iniciar el servidor en el puerto `8000` (el puerto configurado en el backend Java):

```cmd
python main.py
```
*O alternativamente:*
```cmd
uvicorn main:app --reload --port 8000
```

El servicio estará disponible en `http://localhost:8000` y cargará automáticamente la documentación interactiva Swagger en `http://localhost:8000/docs`.

---

## 🔌 Endpoints Implementados (Según el Contrato Trilateral)

### 1. Clasificación NLP en Lote
*   **Método:** `POST`
*   **Ruta:** `/api/v1/classify-transactions`
*   **Request Body:**
    ```json
    {
      "transacciones": [
        { "id": 15, "text": "Supermercado Plaza", "monto": 420.00 },
        { "id": 16, "text": "Gasolinera Repsol", "monto": 300.00 }
      ]
    }
    ```
*   **Response Body (200 OK):**
    ```json
    {
      "clasificados": [
        { "id": 15, "categoriaPredicha": "Alimentación", "monto": 420.00 },
        { "id": 16, "categoriaPredicha": "Transporte", "monto": 300.00 }
      ]
    }
    ```

### 2. Predictor de Salud Financiera (Corazón del Proyecto)
*   **Método:** `POST`
*   **Ruta:** `/api/v1/predict-health`
*   **Request Body:**
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
*   **Response Body (200 OK):**
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

