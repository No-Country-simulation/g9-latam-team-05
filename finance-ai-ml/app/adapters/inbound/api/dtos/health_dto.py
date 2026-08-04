import typing
from pydantic import BaseModel, Field, ConfigDict
import pydantic

class TransactionDetailDTO(pydantic.BaseModel):
    """DTO para el detalle de cada transacción individual recibida."""
    descripcion: str = pydantic.Field(
        ..., 
        min_length=1, 
        description="Descripción o concepto de la transacción",
        json_schema_extra={"example": "Supermercado"}
    )
    valor: float = pydantic.Field(
        ..., 
        gt=0.0, 
        description="Monto monetario de la transacción",
        json_schema_extra={"example": 420.00}
    )


class HealthPredictionRequestDTO(pydantic.BaseModel):
    """DTO de solicitud (Request Body) consumido desde el Backend en Java."""
    ingreso_mensual: float = pydantic.Field(
        ..., 
        gt=0.0, 
        description="Ingreso mensual total del usuario",
        json_schema_extra={"example": 4500.00}
    )
    nivel_endeudamiento: float = pydantic.Field(
        ..., 
        ge=0.0, 
        le=100.0, 
        description="Porcentaje de endeudamiento (0 - 100%)",
        json_schema_extra={"example": 25.00}
    )
    frecuencia_ahorro: str = pydantic.Field(
        ..., 
        description="Frecuencia o nivel de ahorro (Ej: 'Baja', 'Media', 'Alta')",
        json_schema_extra={"example": "Media"}
    )
    transacciones: typing.List[TransactionDetailDTO] = pydantic.Field(
        ..., 
        min_length=1, 
        description="Lista de transacciones registradas en el periodo"
    )

    model_config = pydantic.ConfigDict(
        json_schema_extra={
            "example": {
                "ingreso_mensual": 4500.00,
                "nivel_endeudamiento": 25.00,
                "frecuencia_ahorro": "Media",
                "transacciones": [
                    {"descripcion": "Supermercado", "valor": 420.00},
                    {"descripcion": "Combustible", "valor": 300.00},
                    {"descripcion": "Streaming", "valor": 40.00}
                ]
            }
        }
    )


class ResumenGastosDTO(pydantic.BaseModel):
    """DTO para el desglose consolidado de gastos por categoría oficial de la Hackathon."""
    alimentacion: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en alimentación",
        json_schema_extra={"example": 420.00}
    )
    transporte: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en transporte",
        json_schema_extra={"example": 300.00}
    )
    salud: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en salud",
        json_schema_extra={"example": 150.00}
    )
    vivienda: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en vivienda",
        json_schema_extra={"example": 1200.00}
    )
    educacion: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en educación",
        json_schema_extra={"example": 950.00}
    )
    entretenimiento: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en ocio y entretenimiento",
        json_schema_extra={"example": 40.00}
    )
    servicios: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en servicios básicos",
        json_schema_extra={"example": 80.00}
    )
    otros: float = pydantic.Field(
        default=0.0, 
        ge=0.0, 
        description="Total acumulado en otras categorías",
        json_schema_extra={"example": 0.00}
    )


class HealthPredictionResponseDTO(pydantic.BaseModel):
    """DTO de respuesta (Response Body - 200 OK) devuelto al Backend en Java."""
    perfil_financiero: str = pydantic.Field(
        ..., 
        description="Resultado de la clasificación del perfil de riesgo",
        json_schema_extra={"example": "En observación"}
    )
    probabilidad: float = pydantic.Field(
        ..., 
        ge=0.0, 
        le=1.0, 
        description="Confianza del modelo en la inferencia (0.0 a 1.0)",
        json_schema_extra={"example": 0.82}
    )
    resumen_gastos: typing.Dict[str, float] = pydantic.Field(
        ..., 
        description="Desglose de gastos consolidados por categoría activa (excluye categorías con valor 0)",
        json_schema_extra={
            "example": {
                "alimentacion": 420.00,
                "transporte": 300.00,
                "entretenimiento": 40.00
            }
        }
    )
    recomendaciones: typing.List[str] = pydantic.Field(
        ..., 
        description="Lista de recomendaciones financieras generadas",
        json_schema_extra={
            "example": [
                "Monitorear los gastos recurrentes de entretenimiento",
                "Aumentar la reserva financiera mensual"
            ]
        }
    )

    model_config = pydantic.ConfigDict(
        json_schema_extra={
            "example": {
                "perfil_financiero": "En observación",
                "probabilidad": 0.82,
                "resumen_gastos": {
                    "alimentacion": 420.00,
                    "transporte": 300.00,
                    "entretenimiento": 40.00
                },
                "recomendaciones": [
                    "Monitorear los gastos recurrentes de entretenimiento",
                    "Aumentar la reserva financiera mensual"
                ]
            }
        }
    )