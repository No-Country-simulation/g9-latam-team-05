import oracledb
import os
from dotenv import load_dotenv

load_dotenv(override=True)

user = os.getenv("ORACLE_DB_USER")
password = os.getenv("ORACLE_DB_PASSWORD")
dsn = os.getenv("ORACLE_DB_URL")
wallet_path = os.getenv("ORACLE_WALLET_PATH")
wallet_password = os.getenv("ORACLE_WALLET_PASSWORD")
schema = os.getenv("ORACLE_SCHEMA", "APP_SPRING_BOOT")

def conectar_oracle(verbose=False):
    try:
        connection = oracledb.connect(
            user=user,
            password=password,
            dsn=dsn,
            config_dir=wallet_path,
            wallet_location=wallet_path,
            wallet_password=wallet_password
        )

        # Configuramos el esquema por defecto para que funcione el uso de tablas sin prefijo
        with connection.cursor() as cursor:
            cursor.execute(f"ALTER SESSION SET CURRENT_SCHEMA = {schema}")
        
        # Solo imprime si se solicita explícitamente
        if verbose:
            print("[INFO] Conexión establecida con éxito.")

        return connection
    except Exception as e:
        print(f"Error al conectar: {e}")
        return None