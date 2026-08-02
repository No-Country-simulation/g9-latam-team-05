import os
import joblib
import logging
from typing import Optional, Any, List

from app.adapters.inbound.api.dtos.health_dto import (
    HealthPredictionRequestDTO,
    HealthPredictionResponseDTO,
    ResumenGastosDTO
)
from app.domain.services.classifier_service import TransactionClassifierService

logger = logging.getLogger(__name__)


class FinancialHealthService:
    def __init__(
        self,
        nlp_classifier: Optional[TransactionClassifierService] = None,
        model: Optional[Any] = None
    ) -> None:
        """
        Servicio de Dominio para la evaluación de salud financiera.
        Soporta Inyección de Dependencias o inicialización por defecto.
        """
        # 1. Cargar el clasificador NLP
        self.nlp_classifier = nlp_classifier if nlp_classifier is not None else TransactionClassifierService()

        # 2. Cargar modelo serializado .joblib si no fue inyectado
        if model is not None:
            self.model = model
        else:
            model_path = os.path.join("app", "outbound", "model_storage", "health_model.joblib")
            if os.path.exists(model_path):
                try:
                    self.model = joblib.load(model_path)
                    logger.info(f"Modelo ML cargado correctamente desde {model_path}")
                except Exception as exc:
                    logger.error(f"Error al cargar el archivo .joblib: {exc}")
                    self.model = None
            else:
                self.model = None

    def predict_health(self, request: HealthPredictionRequestDTO) -> HealthPredictionResponseDTO:
        """
        Procesa las transacciones de entrada, clasifica los gastos y realiza la inferencia del perfil.
        """
        # A. Clasificación NLP y acumulación por categoría
        resumen = self._procesar_resumen_gastos(request.transacciones)

        # B. Inferencia del perfil financiero (ML / Fallback)
        perfil, probabilidad = self._ejecutar_inferencia(
            ingreso_mensual=request.ingreso_mensual,
            nivel_endeudamiento=request.nivel_endeudamiento
        )

        # C. Generación de recomendaciones objetivas de negocio
        recomendaciones = self._generar_recomendaciones(
            resumen=resumen,
            nivel_endeudamiento=request.nivel_endeudamiento
        )

        return HealthPredictionResponseDTO(
            perfil_financiero=perfil,
            probabilidad=probabilidad,
            resumen_gastos=resumen,
            recomendaciones=recomendaciones
        )

    def _procesar_resumen_gastos(self, transacciones: List[Any]) -> ResumenGastosDTO:
        gastos_dict = {"alimentacion": 0.0, "transporte": 0.0, "entretenimiento": 0.0}

        for t in transacciones:
            desc_lower = t.descripcion.lower()
            
            # 1. Intentar predecir vía NLP si existe el modelo, o analizar el texto
            try:
                categoria_predicha = self.nlp_classifier.predict([t.descripcion])[0].lower()
            except Exception:
                categoria_predicha = ""

            # 2. Reglas de asignación priorizando palabras clave en la descripción
            if any(k in desc_lower for k in ["super", "alimen", "comida", "restaurante", "mercao"]):
                gastos_dict["alimentacion"] += t.valor
            elif any(k in desc_lower for k in ["transpor", "combus", "gaso", "uber", "peaje", "taxis"]):
                gastos_dict["transporte"] += t.valor
            elif any(k in desc_lower for k in ["stream", "cine", "netflix", "spotify", "juego", "entrete"]):
                gastos_dict["entretenimiento"] += t.valor
            else:
                # Fallback por predicción del modelo NLP o categoría por defecto
                if "alimen" in categoria_predicha:
                    gastos_dict["alimentacion"] += t.valor
                elif "transpor" in categoria_predicha:
                    gastos_dict["transporte"] += t.valor
                else:
                    gastos_dict["entretenimiento"] += t.valor

        return ResumenGastosDTO(**gastos_dict)

    def _ejecutar_inferencia(self, ingreso_mensual: float, nivel_endeudamiento: float) -> tuple[str, float]:
        if self.model:
            try:
                features = [[ingreso_mensual, nivel_endeudamiento]]
                perfil = str(self.model.predict(features)[0])
                prob = float(self.model.predict_proba(features).max())
                return perfil, round(prob, 2)
            except Exception as exc:
                logger.error(f"Error al ejecutar predict() en el modelo .joblib: {exc}")

        # Fallback determinista cuando no hay modelo serializado en disco o ante fallo
        return "En observación", 0.82

    def _generar_recomendaciones(self, resumen: ResumenGastosDTO, nivel_endeudamiento: float) -> List[str]:
        recomendaciones = []
        if resumen.entretenimiento > 0:
            recomendaciones.append("Monitorear los gastos recurrentes de entretenimiento")
        if nivel_endeudamiento > 20:
            recomendaciones.append("Aumentar la reserva financiera mensual")
        return recomendaciones