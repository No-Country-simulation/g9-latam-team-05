# 🐍 Finance AI - Microservicio de Machine Learning (Python FastAPI)

Este es el microservicio oficial en Python FastAPI de Ciencia de Datos y Machine Learning. Contiene el clasificador NLP de transacciones y el modelo Random Forest para la evaluación de salud financiera del usuario.

El proyecto está diseñado siguiendo los principios de la **Arquitectura Hexagonal (Clean Architecture)**.

---

## 🛠️ Requisitos e Instalación (Ejecutar en terminal CMD)

Sigue estos pasos detallados desde el Símbolo del Sistema (**CMD**) de Windows para preparar y ejecutar el entorno:

1. **Abrir la terminal CMD e ir a la carpeta del microservicio:**
   ```cmd
   cd C:\Users\Edison\Downloads\Proyectos\Oracle\g9-latam-team-05\finance-ai-ml
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

5. **Entrenar y serializar el modelo de Salud Financiera:**
   *Este paso es necesario para generar el archivo binario del modelo (`health_model.joblib`) que el servidor cargará al iniciar.*
   ```cmd
   python train_model.py
   ```

---

## 🚀 Ejecutar el Servidor de Inferencia (Ejecutar en terminal CMD)

Una dosificado el entorno y habiendo entrenado el modelo, inicia la API REST en el puerto `8000`:

```cmd
uvicorn main:app --reload --port 8000
```

*   **API Local:** `http://localhost:8000`
*   **Documentación Interactiva (Swagger UI):** `http://localhost:8000/docs`
*   **Health Check de la Arquitectura:** `http://localhost:8000/health`

---

## 🔌 Endpoints del Contrato REST (Inbound Adapters)

### 1. Clasificación NLP en Lote (`POST /api/v1/classify-transactions`)
*   **Body (raw JSON):**
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

### 2. Predictor de Salud Financiera (`POST /api/v1/predict-health`)
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
*   **Response Body (200 OK):**
    ```json
    {
      "perfil_financiero": "En observación",
      "probabilidad": 0.82,
      "resumen_gastos": {
        "alimentacion": 420.00,
        "transporte": 300.00,
        "entretenimiento": 40.00
      },
      "recomendaciones": [
        "Monitorear de cerca tus gastos recurrentes y suscripciones.",
        "Intenta recortar gastos pequeños para aumentar tu tasa de ahorro por encima del 15%.",
        "Establece un fondo de emergencias equivalente a 3 meses de gastos básicos."
      ]
    }
    ```

---

## 📁 Estructura del Directorio del Proyecto

*   `app/adapters/inbound`: Adaptadores HTTP (routers) y modelos Pydantic (DTOs).
*   `app/domain/services`: Lógica de dominio y consumo de modelos matemáticos (Naive Bayes y Random Forest).
*   `app/infrastructure`: Configuración de dependencias e inyección para la app.
*   `app/outbound/model_storage`: Directorio donde se guarda y lee el archivo `health_model.joblib`.
*   `notebooks/01_eda_and_preprocessing.ipynb`: Jupyter Notebook con la exploración y limpieza de datos (EDA) y preprocesamiento.
*   `notebooks/02_model_training_and_evaluation.ipynb`: Jupyter Notebook con el procesamiento de texto, entrenamiento, evaluación (F1-score, accuracy) y serialización de los modelos.
*   `train_model.py`: Script para entrenar el clasificador Random Forest de Salud Financiera.
