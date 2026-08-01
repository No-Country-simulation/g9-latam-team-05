from fastapi import APIRouter
from schemas import ClassifyRequest, ClassifyResponse, ClassifiedItem

router = APIRouter(prefix="/api/v1")

def predict_category(text: str) -> str:
    text_lower = text.lower()
    if any(word in text_lower for word in ["super", "comida", "market", "plaza", "restaurante", "cena", "almuerzo", "desayuno", "vea", "tottus", "metro"]):
        return "Alimentación"
    if any(word in text_lower for word in ["gas", "repsol", "puma", "combustible", "taxi", "uber", "did", "transporte", "pasaje", "bus"]):
        return "Transporte"
    if any(word in text_lower for word in ["netflix", "cine", "streaming", "spotify", "hbo", "disney", "ocio", "juego", "steam", "playstation", "diversion"]):
        return "Ocio"
    if any(word in text_lower for word in ["alquiler", "renta", "luz", "agua", "vivienda", "expensas", "edificio"]):
        return "Vivienda"
    if any(word in text_lower for word in ["medico", "salud", "farmacia", "clinica", "dentista", "doctor", "pastillas", "remedio"]):
        return "Salud"
    if any(word in text_lower for word in ["luz", "agua", "internet", "telefono", "claro", "movistar", "servicios", "cable"]):
        return "Servicios"
    if any(word in text_lower for word in ["colegio", "universidad", "curso", "libro", "educacion", "matricula", "pension"]):
        return "Educación"
    return "Otros"

@router.post("/classify-transactions", response_model=ClassifyResponse)
def classify_transactions(request: ClassifyRequest):
    clasificados = []
    for tx in request.transacciones:
        cat = predict_category(tx.text)
        clasificados.append(ClassifiedItem(
            id=tx.id,
            categoriaPredicha=cat,
            monto=tx.monto
        ))
    return ClassifyResponse(clasificados=clasificados)
