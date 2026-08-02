from pydantic import BaseModel
from typing import List

class TransactionInputDTO(BaseModel):
    id: int
    text: str
    monto: float

class ClassificationRequestDTO(BaseModel):
    transacciones: List[TransactionInputDTO]

class ClassifiedItemDTO(BaseModel):
    id: int
    categoriaPredicha: str
    monto: float

class ClassificationResponseDTO(BaseModel):
    clasificados: List[ClassifiedItemDTO]