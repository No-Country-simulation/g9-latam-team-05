from contextlib import asynccontextmanager
from fastapi import FastAPI

from app.adapters.inbound.api.routers.classifier_router import router as classifier_router
from app.adapters.inbound.api.routers.health_predictor import router as health_router
from app.infrastructure.dependencies import load_ml_models


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Gestor del ciclo de vida de la aplicación.
    Ejecuta el Warm-up de los artefactos ML en memoria RAM antes de recibir tráfico HTTP.
    """
    # Startup: Cargar modelos .joblib / clasificadores en memoria
    load_ml_models()
    yield
    # Shutdown: Liberar recursos si fuera necesario al apagar el servidor
    pass


app = FastAPI(
    title="Finance AI - Microservicio ML",
    description="API para clasificación de transacciones, análisis de perfil y recomendaciones.",
    version="0.1.0",
    lifespan=lifespan
)

# Registrar routers de la capa Inbound (Adaptadores de entrada)
app.include_router(classifier_router)
app.include_router(health_router)


@app.get("/health", tags=["Health Check"])
def health_check():
    return {
        "status": "ok",
        "service": "Finance AI ML Microservice",
        "architecture": "Hexagonal (Ports & Adapters)"
    }


@app.get("/", tags=["Health Check"])
def read_root():
    return {
        "message": "Bienvenido a la API de Finance AI ML. Visita /docs para ver la documentación."
    }