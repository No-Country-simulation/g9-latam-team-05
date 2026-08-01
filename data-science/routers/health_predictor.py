from fastapi import APIRouter
from schemas import PredictHealthRequest, PredictHealthResponse
from typing import Dict, List
from routers.classifier import predict_category

router = APIRouter(prefix="/api/v1")

@router.post("/predict-health", response_model=PredictHealthResponse)
def predict_health(request: PredictHealthRequest):
    # 1. Classify transactions and group by category
    resumen_gastos: Dict[str, float] = {}
    total_gastos = 0.0
    
    for tx in request.transacciones:
        cat = predict_category(tx.descripcion).lower()
        resumen_gastos[cat] = resumen_gastos.get(cat, 0.0) + tx.valor
        total_gastos += tx.valor

    # 2. Heuristics to determine profile
    ingreso = request.ingreso_mensual
    deuda = request.nivel_endeudamiento
    
    if ingreso <= 0:
        perfil = "Crítico"
        probabilidad = 0.95
        recomendaciones = [
            "Tus ingresos registrados son cero. Es indispensable registrar tus ingresos principales.",
            "Evita contraer cualquier deuda adicional de inmediato."
        ]
    else:
        tasa_ahorro = ((ingreso - total_gastos) / ingreso) * 100
        
        if total_gastos > ingreso or deuda > 50:
            perfil = "Crítico"
            probabilidad = 0.88
            recomendaciones = [
                "¡Alerta de déficit! Tus gastos mensuales superan tus ingresos.",
                "Reduce drásticamente los gastos en categorías de ocio y servicios no esenciales.",
                "Evita el uso de tarjetas de crédito y refinancia deudas de alta tasa."
            ]
        elif total_gastos > (ingreso * 0.85) or deuda > 35:
            perfil = "En observación"
            probabilidad = 0.82
            recomendaciones = [
                "Monitorea de cerca tus gastos recurrentes y suscripciones.",
                "Intenta recortar gastos pequeños para aumentar tu tasa de ahorro por encima del 15%.",
                "Establece un fondo de emergencias equivalente a 3 meses de gastos básicos."
            ]
        else:
            perfil = "Saludable"
            probabilidad = 0.90
            recomendaciones = [
                "¡Felicitaciones! Mantienes una excelente tasa de ahorro mensual.",
                "Considera diversificar parte de tu excedente en instrumentos de inversión de bajo riesgo.",
                "Continúa monitoreando tus movimientos financieros con regularidad."
            ]

    # Convert resume keys to Spanish match exactly
    # Let's ensure standard keys: 'alimentacion', 'transporte', 'ocio', 'vivienda', 'salud', 'servicios', 'educacion', 'otros'
    resumen_formateado = {
        key.replace("ó", "o").replace(" ", "_"): val for key, val in resumen_gastos.items()
    }

    return PredictHealthResponse(
        perfil_financiero=perfil,
        probabilidad=probabilidad,
        resumen_gastos=resumen_formateado,
        recomendaciones=recomendaciones
    )
