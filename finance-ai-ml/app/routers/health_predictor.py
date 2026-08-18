from fastapi import APIRouter, status
from app.schemas.health_dto import (
    HealthPredictionRequestDTO,
    HealthPredictionResponseDTO
)
from app.services.health_service import FinancialHealthService

router = APIRouter(prefix="/api/v1", tags=["Perfil de Salud IA"])
health_service = FinancialHealthService()

@router.post(
    "/predict-health",
    response_model=HealthPredictionResponseDTO,
    status_code=status.HTTP_200_OK,
    summary="Predecir Perfil de Salud Financiera"
)
def predict_health(request: HealthPredictionRequestDTO) -> HealthPredictionResponseDTO:
    return health_service.predict_health(request)
