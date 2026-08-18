from fastapi import APIRouter
from app.schemas.classifier_dto import (
    ClassificationRequestDTO, 
    ClassificationResponseDTO, 
    ClassifiedItemDTO
)
from app.services.classifier_service import TransactionClassifierService

router = APIRouter(prefix="/api/v1", tags=["Clasificador NLP"])
classifier_service = TransactionClassifierService()

@router.post("/classify-transactions", response_model=ClassificationResponseDTO)
def classify_transactions(request: ClassificationRequestDTO):
    texts = [t.text for t in request.transacciones]
    predictions = classifier_service.predict(texts)
    
    clasificados_res = [
        ClassifiedItemDTO(
            id=transaccion.id,
            categoriaPredicha=pred,
            monto=transaccion.monto
        )
        for transaccion, pred in zip(request.transacciones, predictions)
    ]
    
    return ClassificationResponseDTO(clasificados=clasificados_res)
