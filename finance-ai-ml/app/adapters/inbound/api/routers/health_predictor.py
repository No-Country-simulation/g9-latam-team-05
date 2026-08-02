from fastapi import APIRouter, Depends, status
from app.adapters.inbound.api.dtos.health_dto import (
    HealthPredictionRequestDTO,
    HealthPredictionResponseDTO
)
from app.domain.services.health_service import FinancialHealthService
from app.infrastructure.dependencies import get_health_service

router = APIRouter(
    prefix="/api/v1",
    tags=["Perfil de Salud IA"]
)

@router.post(
    "/predict-health",
    response_model=HealthPredictionResponseDTO,
    status_code=status.HTTP_200_OK,
    summary="Predecir Perfil de Salud Financiera",
    description="Endpoint consumido por el Microservicio Java para clasificar transacciones e inferir el riesgo/salud financiera mediante modelos ML."
)
def predict_health(
    request: HealthPredictionRequestDTO,
    service: FinancialHealthService = Depends(get_health_service)
) -> HealthPredictionResponseDTO:
    return service.predict_health(request)