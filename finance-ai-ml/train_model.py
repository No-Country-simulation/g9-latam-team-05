import os
import joblib
import numpy as np
from sklearn.ensemble import RandomForestClassifier


def train_and_save_model():
    print("🚀 Iniciando el entrenamiento del modelo de Salud Financiera...")

    # 1. Dataset de entrenamiento (Features: [ingreso_mensual, nivel_endeudamiento_%])
    X_train = np.array([
        # Perfil: Saludable / Excelente
        [8000.0, 10.0],
        [6000.0, 15.0],
        [5000.0, 18.0],
        [4500.0, 12.0],
        
        # Perfil: En observación
        [4500.0, 25.0],
        [3500.0, 30.0],
        [2800.0, 35.0],
        [5000.0, 40.0],
        
        # Perfil: En riesgo
        [3000.0, 55.0],
        [2000.0, 60.0],
        [1500.0, 75.0],
        [1200.0, 80.0]
    ])

    # Etiquetas correspondientes
    y_train = np.array([
        "Excelente", "Excelente", "Excelente", "Excelente",
        "En observación", "En observación", "En observación", "En observación",
        "En riesgo", "En riesgo", "En riesgo", "En riesgo"
    ])

    # 2. Entrenar el clasificador Random Forest
    clf = RandomForestClassifier(n_estimators=100, random_state=42)
    clf.fit(X_train, y_train)

    # 3. Definir la ruta de salida (app/outbound/model_storage/)
    output_dir = os.path.join("app", "outbound", "model_storage")
    os.makedirs(output_dir, exist_ok=True)
    model_path = os.path.join(output_dir, "health_model.joblib")

    # 4. Serializar y guardar el archivo binario .joblib
    joblib.dump(clf, model_path)
    print(f"✅ ¡Modelo ML entrenado y guardado exitosamente en: {model_path}!")


if __name__ == "__main__":
    train_and_save_model()