import os
import joblib
import logging
import numpy as np
import pandas as pd
from typing import List, Any
from app.schemas.health_dto import (
    HealthPredictionRequestDTO,
    HealthPredictionResponseDTO
)
from app.services.classifier_service import TransactionClassifierService

logger = logging.getLogger("uvicorn.error")

class FinancialHealthService:
    """
    Servicio de Inteligencia Artificial para inferencia del Perfil de Salud Financiera
    mediante Random Forest Classifier y clasificación de transacciones con NLP.
    """
    def __init__(self):
        self.nlp_classifier = TransactionClassifierService()
        self.model = None
        self._cargar_modelo()

    def _cargar_modelo(self):
        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        model_path = os.path.join(base_dir, "models", "health_model.joblib")
        if os.path.exists(model_path):
            try:
                self.model = joblib.load(model_path)
                logger.info(f"Modelo ML cargado correctamente desde {model_path}")
            except Exception as exc:
                logger.error(f"Error al cargar el archivo .joblib: {exc}")
                self.model = None

    def predict_health(self, request: HealthPredictionRequestDTO) -> HealthPredictionResponseDTO:
        # 1. Clasificación NLP de transacciones y cálculo del resumen de gastos
        resumen_gastos, gasto_total = self._procesar_resumen_gastos(request.transacciones)

        # 2. Inferencia con Modelo ML (Random Forest Classifier)
        perfil, probabilidad = self._ejecutar_inferencia_ml(
            ingreso_mensual=request.ingreso_mensual,
            nivel_endeudamiento=request.nivel_endeudamiento,
            frecuencia_ahorro=request.frecuencia_ahorro,
            gasto_total=gasto_total
        )

        # 3. Generación de recomendaciones personalizadas basadas en patrones de IA
        recomendaciones = self._generar_recomendaciones(
            perfil=perfil,
            resumen=resumen_gastos,
            ingreso_mensual=request.ingreso_mensual,
            nivel_endeudamiento=request.nivel_endeudamiento,
            gasto_total=gasto_total
        )

        return HealthPredictionResponseDTO(
            perfil_financiero=perfil,
            probabilidad=probabilidad,
            resumen_gastos=resumen_gastos,
            recomendaciones=recomendaciones
        )

    def _procesar_resumen_gastos(self, transacciones: List[Any]) -> tuple[dict, float]:
        gastos_dict = {
            "Alimentación": 0.0,
            "Transporte": 0.0,
            "Salud": 0.0,
            "Vivienda": 0.0,
            "Educación": 0.0,
            "Entretenimiento": 0.0,
            "Servicios": 0.0,
            "Otros": 0.0
        }
        gasto_total = 0.0

        for t in transacciones:
            try:
                categoria_predicha = self.nlp_classifier.predict([t.descripcion])[0]
                monto = float(t.valor)
                gasto_total += monto

                if categoria_predicha in gastos_dict:
                    gastos_dict[categoria_predicha] += monto
                else:
                    gastos_dict["Otros"] += monto
            except Exception as exc:
                logger.error(f"Error clasificando transaccion '{t.descripcion}': {exc}")
                gastos_dict["Otros"] += float(t.valor)
                gasto_total += float(t.valor)

        resumen_filtrado = {k: round(v, 2) for k, v in gastos_dict.items() if v > 0.0}
        return resumen_filtrado, round(gasto_total, 2)

    def _ejecutar_inferencia_ml(self, ingreso_mensual: float, nivel_endeudamiento: float, frecuencia_ahorro: str, gasto_total: float) -> tuple[str, float]:
        """
        Ejecuta el modelo de Machine Learning entrenado (Random Forest)
        sobre el vector de características financieras del usuario.
        """
        frec_map = {"baja": 0, "media": 1, "alta": 2}
        frec_num = frec_map.get(str(frecuencia_ahorro).lower().strip(), 1)

        ratio_gasto_ingreso = (gasto_total / ingreso_mensual) if ingreso_mensual > 0 else 1.0
        ahorro_neto = ingreso_mensual - gasto_total

        df_input = pd.DataFrame([{
            'ingreso_mensual': float(ingreso_mensual),
            'nivel_endeudamiento': float(nivel_endeudamiento),
            'frecuencia_ahorro': int(frec_num),
            'gasto_total': float(gasto_total),
            'ratio_gasto_ingreso': float(ratio_gasto_ingreso),
            'ahorro_neto': float(ahorro_neto)
        }])

        if self.model:
            try:
                perfil_predicho = str(self.model.predict(df_input)[0])
                probabilidades = self.model.predict_proba(df_input)[0]
                confianza = float(np.max(probabilidades))
                return perfil_predicho, round(confianza, 2)
            except Exception as exc:
                logger.error(f"Error en predict() de Random Forest: {exc}")

        # Fallback determinista en caso de contingencia técnica
        if nivel_endeudamiento > 45 or ratio_gasto_ingreso > 0.95 or ahorro_neto < 0:
            return "En riesgo", 0.85
        elif nivel_endeudamiento <= 25 and ratio_gasto_ingreso <= 0.70:
            return "Saludable", 0.90
        else:
            return "En observación", 0.82

    def _generar_recomendaciones(self, perfil: str, resumen: dict, ingreso_mensual: float, nivel_endeudamiento: float, gasto_total: float) -> List[str]:
        recomendaciones = []
        
        # Análisis de categorías de mayor impacto detectadas por NLP
        if resumen.get("Entretenimiento", 0.0) > (ingreso_mensual * 0.15):
            recomendaciones.append("Monitorear los gastos recurrentes de ocio y suscripciones de entretenimiento.")
        elif resumen.get("Entretenimiento", 0.0) > 0:
            recomendaciones.append("Mantener un presupuesto fijo para ocio sin exceder el 10% de tus ingresos.")

        if resumen.get("Alimentación", 0.0) > (ingreso_mensual * 0.35):
            recomendaciones.append("Optimizar las compras recurrentes de supermercado mediante planificación semanal.")

        if resumen.get("Transporte", 0.0) > (ingreso_mensual * 0.20):
            recomendaciones.append("Evaluar alternativas de movilidad o recargas semanales para reducir el gasto en transporte.")

        # Recomendaciones de perfil y endeudamiento
        if nivel_endeudamiento > 35:
            recomendaciones.append("Priorizar la amortización de deudas con mayor tasa de interés para reducir el endeudamiento.")
        elif nivel_endeudamiento > 20:
            recomendaciones.append("Aumentar la reserva financiera mensual y evitar nuevas compras a crédito.")

        if (ingreso_mensual - gasto_total) < 0:
            recomendaciones.append("Ajustar el presupuesto mensual urgente: los gastos actuales superan los ingresos.")
        elif (ingreso_mensual - gasto_total) > (ingreso_mensual * 0.20):
            recomendaciones.append("¡Excelente capacidad de ahorro! Considera invertir tus excedentes en fondos o depósitos a plazo.")

        if not recomendaciones:
            recomendaciones.append("Mantener el balance actual y continuar registrando tus movimientos periódicamente.")

        return recomendaciones
