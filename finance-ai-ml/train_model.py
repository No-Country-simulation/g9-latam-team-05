# -*- coding: utf-8 -*-
import os
import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report

def train_and_save_model():
    print("[INFO] Generando dataset sintetico de perfiles financieros con distribucion balanceada...")
    np.random.seed(42)
    n_samples = 4500

    # 1. Generacion de perfiles realistas
    ingreso = np.random.uniform(1200, 15000, n_samples)
    endeudamiento = np.random.uniform(0, 90, n_samples)
    frec_map = {'Baja': 0, 'Media': 1, 'Alta': 2}
    frecuencias = np.random.choice(['Baja', 'Media', 'Alta'], size=n_samples, p=[0.33, 0.34, 0.33])
    frecuencia_num = np.array([frec_map[f] for f in frecuencias])

    ratio_gasto = np.random.uniform(0.30, 1.20, n_samples)
    gasto_total = ingreso * ratio_gasto
    ahorro_neto = ingreso - gasto_total

    labels = []
    for i in range(n_samples):
        deuda = endeudamiento[i]
        rg = ratio_gasto[i]
        frec = frecuencia_num[i]
        ahorro = ahorro_neto[i]

        # Criterios bancarios y de riesgo financiero
        if deuda > 45 or rg > 0.95 or ahorro < 0:
            labels.append("En riesgo")
        elif deuda <= 25 and rg <= 0.70 and frec >= 1 and ahorro > 0:
            labels.append("Saludable")
        else:
            labels.append("En observación")

    df = pd.DataFrame({
        'ingreso_mensual': ingreso,
        'nivel_endeudamiento': endeudamiento,
        'frecuencia_ahorro': frecuencia_num,
        'gasto_total': gasto_total,
        'ratio_gasto_ingreso': ratio_gasto,
        'ahorro_neto': ahorro_neto,
        'perfil': labels
    })

    X = df[['ingreso_mensual', 'nivel_endeudamiento', 'frecuencia_ahorro', 'gasto_total', 'ratio_gasto_ingreso', 'ahorro_neto']]
    y = df['perfil']

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

    print("[INFO] Entrenando Random Forest Classifier (150 arboles)...")
    clf = RandomForestClassifier(n_estimators=150, max_depth=12, random_state=42, class_weight='balanced')
    clf.fit(X_train, y_train)

    y_pred = clf.predict(X_test)
    print("\n--- Metricas de Rendimiento del Modelo de Perfil Financiero ---")
    print(classification_report(y_test, y_pred))

    output_dir = os.path.join(os.path.dirname(__file__), "app", "models")
    os.makedirs(output_dir, exist_ok=True)
    model_path = os.path.join(output_dir, "health_model.joblib")

    joblib.dump(clf, model_path)
    print(f"[OK] Modelo serializado y guardado exitosamente en: {model_path}\n")

if __name__ == "__main__":
    train_and_save_model()
