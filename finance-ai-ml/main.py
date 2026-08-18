from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers.classifier_router import router as classifier_router
from app.routers.health_predictor import router as health_router

app = FastAPI(
    title="Finance AI - Microservicio ML",
    description="API para clasificación de transacciones mediante NLP y evaluación de salud financiera.",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(classifier_router)
app.include_router(health_router)

@app.get("/health", tags=["Health Check"])
def health_check():
    return {
        "status": "ok",
        "service": "Finance AI ML Microservice"
    }

@app.get("/", tags=["Health Check"])
def read_root():
    return {
        "message": "Bienvenido a la API de Finance AI ML. Visita /docs para interactuar con Swagger."
    }
