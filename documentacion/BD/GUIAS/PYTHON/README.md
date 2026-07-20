# Guía de Configuración: Entorno Python para Oracle DB

Esta guía detalla los pasos necesarios para configurar el entorno de ejecución necesario para interactuar con la base de datos Oracle desde Python.

## 1. Requisitos previos
* Python 3.x
* Acceso a la base de datos y al archivo Wallet (credenciales de OCI).

## 2. Instalación de dependencias
Para instalar las librerías necesarias, ejecuta el siguiente comando en la terminal dentro de esta carpeta:

```bash
pip install -r requirements.txt
```

## 3. Configuración de variables de entorno
1. Copia el archivo .env_example y renómbralo a .env en tu directorio local.
2. Abre el archivo .env y completa los campos con tus credenciales reales:

| Variable | Descripción |
| :--- | :--- |
| ORACLE_DB_USER | Tu usuario de base de datos. |
| ORACLE_DB_PASSWORD | Tu contraseña. |
| ORACLE_DB_URL | Nombre del servicio o URL de conexión. |
| ORACLE_WALLET_PATH | Ruta absoluta a la carpeta del Wallet. |
| ORACLE_WALLET_PASSWORD | Contraseña del Wallet. |
| ORACLE_SCHEMA | Esquema por defecto (ej: APP_SPRING_BOOT). |

## 4. Estructura de archivos
* conectar_oracle_test.py: Módulo central de conexión.
* db_utils.py: Funciones utilitarias para listar, insertar y gestionar tablas.
* requirements.txt: Lista de dependencias del proyecto.
* .env_example: Plantilla de variables de entorno requeridas.