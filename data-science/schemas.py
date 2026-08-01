from pydantic import BaseModel, Field
from typing import List, Dict

# Classifier schemas
class TransactionInput(BaseModel):
    id: int
    text: str
    monto: float = 0.0

class ClassifyRequest(BaseModel):
    transacciones: List[TransactionInput]

class ClassifiedItem(BaseModel):
    id: int
    categoriaPredicha: str
    monto: float

class ClassifyResponse(BaseModel):
    clasificados: List[ClassifiedItem]


# Health predictor schemas
class TransactionDetailInput(BaseModel):
    descripcion: str
    valor: float

class PredictHealthRequest(BaseModel):
    ingreso_mensual: float
    nivel_endeudamiento: float
    frecuencia_ahorro: str
    transacciones: List[TransactionDetailInput]

class PredictHealthResponse(BaseModel):
    perfil_financiero: str
    probabilidad: float
    resumen_gastos: Dict[str, float]
    recomendaciones: List[str]
