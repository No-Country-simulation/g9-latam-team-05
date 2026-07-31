import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import classifier, health_predictor

app = FastAPI(
    title="Finance AI - Python ML Service Mock",
    description="Servicio mock de Data Science para clasificación NLP y predicción de salud financiera",
    version="1.0.0"
)

# Enable CORS for local development and backend integration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(classifier.router)
app.include_router(health_predictor.router)

@app.get("/")
def read_root():
    return {
        "status": "online",
        "service": "Finance AI - Data Science Backend",
        "endpoints": [
            "POST /api/v1/classify-transactions",
            "POST /api/v1/predict-health"
        ]
    }

if __name__ == "__main__":
    # Runs FastAPI on localhost:8000
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
