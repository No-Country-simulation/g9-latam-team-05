import os
import joblib
import logging
from typing import Optional, Any
from app.domain.services.classifier_service import TransactionClassifierService
from app.domain.services.health_service import FinancialHealthService

logger = logging.getLogger(__name__)

# Singletons en memoria
_MODEL_INSTANCE: Optional[Any] = None
_CLASSIFIER_INSTANCE: Optional[TransactionClassifierService] = None


def load_ml_models() -> None:
    """Carga inicial de artefactos ML al arrancar el servidor (Warm-up)."""
    global _MODEL_INSTANCE, _CLASSIFIER_INSTANCE

    _CLASSIFIER_INSTANCE = TransactionClassifierService()

    model_path = os.path.join("app", "outbound", "model_storage", "health_model.joblib")
    if os.path.exists(model_path):
        try:
            _MODEL_INSTANCE = joblib.load(model_path)
            logger.info(f"Modelo .joblib cargado exitosamente desde: {model_path}")
        except Exception as e:
            logger.error(f"Error cargando el modelo ML: {e}")
            _MODEL_INSTANCE = None
    else:
        logger.warning(f"No se encontró binario en {model_path}. Se usará la estrategia Fallback.")


def get_health_service() -> FinancialHealthService:
    """Inyector de dependencias para FastAPI."""
    if _CLASSIFIER_INSTANCE is None:
        load_ml_models()

    return FinancialHealthService(
        nlp_classifier=_CLASSIFIER_INSTANCE,
        model=_MODEL_INSTANCE
    )