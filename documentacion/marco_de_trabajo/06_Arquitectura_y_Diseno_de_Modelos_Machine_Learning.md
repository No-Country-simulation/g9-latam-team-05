# 🧠 06. Arquitectura y Diseño de Modelos de Machine Learning (Entregable Ciencia de Datos)

Este documento detalla la arquitectura de modelado de datos, algoritmos, métricas de rendimiento y la estrategia de serialización utilizada en el componente de **Ciencia de Datos / Machine Learning** del proyecto **Finance AI**, de acuerdo con las especificaciones oficiales de la Hackathon.

---

## 📊 1. Exploración, Limpieza de Datos e Ingeniería de Atributos

### A. Dataset Utilizado
*   **Origen:** `Personal_Finance_Dataset.csv` (1,500 registros y 5 columnas originales).
*   **Columnas Procesadas:**
    *   `fecha` (DateTime): Conversión de la fecha del movimiento.
    *   `monto` (Float): Importe de la transacción.
    *   `categoria` (String): Categoría nominal original.
    *   `descripcion` (String): Texto libre del concepto del gasto.
    *   `frecuencia_ahorro` (String): Variable objetivo categórica derivada.

### B. Ingeniería de Atributos (Feature Engineering)
Se construyó una lógica de discretización para la variable `frecuencia_ahorro` basada en el volumen del monto mensual de transacciones:
*   **Baja:** `monto < 500`
*   **Media:** `500 <= monto <= 2000`
*   **Alta:** `monto > 2000`

---

## 🔤 2. Modelo 1: Clasificador NLP de Gastos (Procesamiento de Lenguaje)

Este modelo automatiza la asignación de categorías financieras a partir del texto de la transacción registrado por el usuario o importado desde un archivo CSV.

### A. Algoritmo y Representación
*   **Vectorización de Texto:** **TF-IDF Vectorizer** (`TfidfVectorizer` con `ngram_range=(1, 2)`). Convierte el texto libre en una matriz de pesos numéricos ponderados por la frecuencia de las palabras y su importancia en los textos.
*   **Clasificador:** **Multinomial Naive Bayes** (`MultinomialNB`). Un algoritmo probabilístico rápido, ideal para clasificación de texto y procesamiento del lenguaje natural (NLP) en tiempo real con baja latencia.

### B. Muestras de Clasificación NLP
El modelo mapea descripciones reales a las categorías del caso de uso de la Hackathon:
*   *"Supermercado Plaza"*, *"Compra Metro"* ➔ **Alimentación**
*   *"Gasolinera Repsol"*, *"Viaje Uber"* ➔ **Transporte**
*   *"Consulta Clinica"*, *"Farmacia"* ➔ **Salud**
*   *"Pago Alquiler"*, *"Renta departamento"* ➔ **Vivienda**
*   *"Pension Colegio"*, *"Matricula Universidad"* ➔ **Educación**
*   *"Suscripción Netflix"*, *"Entradas Cine"* ➔ **Ocio (Entretenimiento)**
*   *"Recibo de luz Enel"*, *"Pago Agua"* ➔ **Servicios**

---

## 📈 3. Modelo 2: Predictor del Perfil de Salud Financiera (Corazón del MVP)

Este modelo toma las variables del perfil del usuario y el historial consolidado del mes para predecir su nivel de riesgo y salud financiera.

### A. Características de Entrada (Features)
1.  `ingreso_mensual` (Float): Ingreso percibido.
2.  `nivel_endeudamiento` (Float): Porcentaje de deudas vigentes frente al ingreso (0.0 a 100.0).

### B. Algoritmo de Inferencia
*   **Modelo:** **Random Forest Classifier** (`RandomForestClassifier` con 100 estimadores).
*   **Justificación:** Al ser un ensamble de múltiples árboles de decisión (Bagging), reduce drásticamente el sobreajuste (overfitting), maneja relaciones no lineales entre las variables y ofrece probabilidades de clasificación sumamente robustas.

### C. Categorías del Perfil de Salida
1.  **Saludable:** Usuario con ingresos altos/estables y nivel de endeudamiento menor a 20%.
2.  **En observación:** Usuario con ingresos medios y nivel de endeudamiento entre 20% y 50%.
3.  **En riesgo (Crítico):** Usuario con nivel de endeudamiento superior a 50% o gastos mensuales que superan sus ingresos.

---

## 🧪 4. Métricas de Rendimiento y Evaluación

Para asegurar que los modelos sean óptimos y confiables ante la evaluación de los jueces, se aplican las siguientes métricas en el conjunto de prueba (Test Split - 20%):

1.  **Accuracy (Exactitud):** Mide la proporción de predicciones correctas del perfil y de categorías.
2.  **F1-Score (Medida Armónica):** Cruza la Precisión (Precision) y la Sensibilidad (Recall) para garantizar un balance, especialmente útil si hay clases desbalanceadas en el dataset de finanzas.
3.  **Matriz de Confusión:** Graficada para visualizar los errores de clasificación entre las categorías.

---

## 💾 5. Estrategia de Serialización e Integración

*   **Serialización:** Se utiliza la librería **`joblib`** para serializar y guardar los modelos entrenados y los transformadores en binarios ejecutables:
    *   `health_model.joblib`: Modelo clasificador de salud financiera listo para producción.
*   **Flujo de Inferencia en Producción:**
    El backend Java Spring Boot carga el modelo `.joblib` en memoria al inicializar el servidor (o consulta a Python FastAPI quien mantiene el modelo cargado mediante el lifespan de FastAPI), garantizando un tiempo de respuesta de inferencia menor a **50 milisegundos**.
