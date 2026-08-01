from conectar_oracle_test import conectar_oracle

def listar_tabla(nombre_tabla):
    """
    Parámetros:
    nombre_tabla (str): Nombre de la tabla a consultar. 
    """
    with conectar_oracle() as conn:
        with conn.cursor() as cur:
            try:
                # Construcción de la consulta dinámica
                query = f"SELECT * FROM {nombre_tabla}"
                cur.execute(query)
                
                # Obtiene los nombres de las columnas directamente del cursor
                columnas = [desc[0] for desc in cur.description]
                
                print(f"\n--- Tabla: {nombre_tabla} ---")
                print(" | ".join(columnas))
                print("-" * 40)
                
                # Recorre e imprime cada fila
                for row in cur.fetchall():
                    print(" | ".join(map(str, row)))
            
            except Exception as e:
                # ORA-00942: Tabla o vista no existe
                if "ORA-00942" in str(e):
                    print(f"\n[ERROR]: La tabla '{nombre_tabla}' no existe o no tienes permisos para acceder a ella.")
                else:
                    print(f"\nError inesperado al listar '{nombre_tabla}': {e}")

def insertar_datos(nombre_tabla, datos):
    """
    Operación DML: Inserta un registro en la tabla especificada.
    
    Parámetros:
    nombre_tabla (str): Nombre de la tabla (ej: 'APP_SPRING_BOOT.USUARIOS').
    datos (dict): Diccionario con columnas y valores (ej: {'NOMBRE': 'Juan', 'ID': 10}).
    
    Nota: Asegúrate de incluir todas las columnas que tengan restricción NOT NULL.
    """
    columnas = ", ".join(datos.keys())
    valores = ", ".join([f":{k}" for k in datos.keys()])
    
    query = f"INSERT INTO {nombre_tabla} ({columnas}) VALUES ({valores})"
    
    with conectar_oracle() as conn:
        with conn.cursor() as cur:
            try:
                cur.execute(query, datos)
                conn.commit()
                print(f"\nÉxito: Registro insertado en '{nombre_tabla}'.")
            except Exception as e:
                error_str = str(e)
                # ORA-00001: Violación de restricción única (ej: ID duplicado)
                if "ORA-00001" in error_str:
                    print(f"\n[ERROR]: El registro ya existe en '{nombre_tabla}' (violación de clave única).")
                # ORA-01400: Columna obligatoria vacía
                elif "ORA-01400" in error_str:
                    print(f"\n[ERROR]: Faltan campos obligatorios para insertar en '{nombre_tabla}'.")
                else:
                    print(f"\nError al insertar en '{nombre_tabla}': {e}")

def eliminar_tabla(nombre_tabla):
    """
    Operación DDL: Eliminación de estructura (DROP).
    Si el nombre no tiene esquema, se asume AI_PYTHON_USER.
    
    Nota: La función fallará si la tabla no existe en la base de datos.
    """
    # 1. Asegurar que trabajamos con el nombre completo (ESQUEMA.TABLA)
    if "." not in nombre_tabla:
        tabla_final = f"AI_PYTHON_USER.{nombre_tabla}"
    else:
        tabla_final = nombre_tabla

    # 2. Validación de seguridad para evitar errores de permisos
    if "APP_SPRING_BOOT" in tabla_final.upper():
        print("\n[ERROR]: Permiso denegado. No tienes privilegios para hacer DROP en APP_SPRING_BOOT.")
        return

    with conectar_oracle() as conn:
        with conn.cursor() as cur:
            try:
                cur.execute(f"DROP TABLE {tabla_final}")
                conn.commit()
                print(f"\nÉxito: La tabla '{tabla_final}' ha sido eliminada.")
            except Exception as e:
                # ORA-00942 "tabla no existe"
                if "ORA-00942" in str(e):
                    print(f"\nAviso: La tabla '{tabla_final}' ya no existe (probablemente ya fue eliminada).")
                else:
                    print(f"\nError inesperado al intentar eliminar: {e}")

def eliminar_todos_los_registros(nombre_tabla):
    """
    Operación DML: Borra todos los datos de la tabla pero mantiene la estructura.
    Permitido en: APP_SPRING_BOOT y AI_PYTHON_USER.
    """
    with conectar_oracle() as conn:
        with conn.cursor() as cur:
            try:
                # DELETE FROM sin WHERE borra todas las filas
                cur.execute(f"DELETE FROM {nombre_tabla}")
                conn.commit()
                print(f"\nÉxito: Todos los registros de '{nombre_tabla}' fueron eliminados.")
            except Exception as e:
                print(f"\nError al intentar vaciar la tabla '{nombre_tabla}': {e}")

def listar_tablas_en_esquema(esquema):
    """
    Lista todas las tablas visibles para el usuario en un esquema determinado.
    """
    query = """
    SELECT table_name 
    FROM all_tables 
    WHERE owner = :esquema 
    ORDER BY table_name
    """
    with conectar_oracle() as conn:
        with conn.cursor() as cur:
            cur.execute(query, {'esquema': esquema.upper()})
            tablas = cur.fetchall()
            
            print(f"\n--- Tablas en esquema: {esquema.upper()} ---")
            if tablas:
                for tabla in tablas:
                    print(f" - {tabla[0]}")
            else:
                print(" No se encontraron tablas o el esquema no existe.")

def listar_entornos():
    """
    Muestra rápidamente las tablas de los dos entornos de trabajo.
    """
    print("\n[INFO] Explorando entornos de base de datos...")
    listar_tablas_en_esquema("AI_PYTHON_USER")
    listar_tablas_en_esquema("APP_SPRING_BOOT")


if __name__ == "__main__":

    # --- PRUEBA ESQUEMA---
    #listar_entornos()

    # --- PRUEBAS DE LECTURA ---
    #listar_tabla("USUARIOS") # Entorno: APP_SPRING_BOOT
    #listar_tabla("AI_PYTHON_USER.TABLA_DS")

    # --- PRUEBAS DE ESCRITURA ---
    # nuevo_usuario = {
    #     'ID': 104, 
    #     'NOMBRE': 'user104', 
    #     'EMAIL': 'user104@test.com', 
    #     'ESTADO': 'ACTIVO',
    #     'PASSWORD_HASH': 'hash_temporal_123'
    # }
    # insertar_datos("USUARIOS", nuevo_usuario)

    # --- PRUEBA DE ELIMINACIÓN TABLA EN AI_PYTHON_USER ENTORNO ---
    #eliminar_tabla("AI_PYTHON_USER.NOMBRE_DE_LA_TABLA")

    # --- PRUEBA DE ELIMINACIÓN REGISTROS---
    #eliminar_todos_los_registros("USUARIOS")

    

    print("db_utils cargado correctamente")