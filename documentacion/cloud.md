# ☁️ Documentación de Despliegue en Cloud (Oracle Cloud Infrastructure - OCI)

Para consultar la guía técnica completa de arquitectura, variables de entorno, puertos, despliegue de Oracle Autonomous Database, artefactos ML en OCI Object Storage y la configuración trilateral de servicios, revisa el archivo oficial:

👉 **[07_Plan_de_Despliegue_OCI_y_Servicios_Trilaterales.md](file:///c:/Users/Usuario/Downloads/Proyectos/oracleHackathon/g9-latam-team-05/documentacion/marco_de_trabajo/07_Plan_de_Despliegue_OCI_y_Servicios_Trilaterales.md)**

---

## 📌 Resumen de Puertos y Archivos de Configuración en OCI

| Componente | Archivo de Configuración | Puerto Local | Puerto Producción (OCI) | Host / URL |
| :--- | :--- | :---: | :---: | :--- |
| **Frontend (Angular)** | `frontend/src/environments/environment.prod.ts` | `4200` | `80` / `443` | `http://<IP_PUBLICA_OCI>` |
| **Backend (Java Spring Boot)** | `backend/src/main/resources/application.properties` | `8080` | `8080` | `http://<IP_PUBLICA_OCI>:8080` |
| **Backend ML (Python FastAPI)** | `finance-ai-ml/.env` / `main.py` | `8000` | `8000` | `http://<IP_PUBLICA_OCI>:8000` |
| **Base de Datos (Autonomous DB)** | Wallet mTLS / OCI DB Console | `1521` / `5432` | `1521` | `<DB_HOST_OCI>` |
