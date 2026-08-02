import pandas as pd
import numpy as np
import random

# Definir listas de descripciones realistas en español para cada categoría
descripciones_por_categoria = {
    'Food & Drink': [
        "Compra Supermercado Metro", "Compra Plaza Vea", "Supermercado Tottus", "Compra en Wong",
        "Cena en restaurante rústica", "Almuerzo menú criollo", "Cafetería Starbucks", "Desayuno panadería",
        "McDonald's combo familiar", "KFC almuerzo", "Compra de víveres minimarket", "Pizza Hut familiar"
    ],
    'Travel': [
        "Servicio Taxi Uber", "Combustible estación Repsol", "Estación de servicio Primax",
        "Pasaje de bus interprovincial", "Recarga tarjeta Metropolitano", "Peaje vía evitamiento",
        "Servicio Taxi Cabify", "Pasaje aéreo Latam", "Recarga de gasolina"
    ],
    'Health & Fitness': [
        "Consulta clínica San Felipe", "Compra medicamentos Inkafarma", "Medicinas Mifarma",
        "Membresía Gimnasio Smartfit", "Consulta odontológica", "Análisis de laboratorio",
        "Consulta pediatra", "Compra de vitaminas"
    ],
    'Rent': [
        "Pago alquiler departamento", "Renta mensual de vivienda", "Pago mensual de habitación",
        "Alquiler de local comercial"
    ],
    'Utilities': [
        "Recibo de luz Enel", "Pago de agua Sedapal", "Recibo de internet Movistar",
        "Pago de telefonía Claro", "Recibo de gas Calidda", "Pago de tv cable DirecTV"
    ],
    'Entertainment': [
        "Suscripción Netflix mensual", "Suscripción Spotify familiar", "Entradas Cineplanet",
        "Suscripción Disney Plus", "Entrada concierto", "Compra juego de PlayStation",
        "Suscripción HBO Max"
    ],
    'Shopping': [
        "Tienda de ropa Saga Falabella", "Compra online Amazon", "Tienda por departamento Ripley",
        "Compra de zapatillas Adidas", "Compra de laptop Wilson", "Artículos de casa Sodimac"
    ],
    'Other': [
        "Otros gastos varios", "Retiro de efectivo cajero", "Comisión bancaria mensual",
        "Trámite notarial", "Fotocopias e impresiones", "Envío de encomienda"
    ],
    'Salary': [
        "Pago de haberes sueldo", "Depósito nómina mensual", "Honorarios profesionales",
        "Pago por consultoría externa"
    ],
    'Investment': [
        "Depósito plazo fijo", "Compra de acciones bolsa de valores", "Inversión fondo mutuo",
        "Aporte voluntario AFP"
    ],
    'Education': [
        "Pensión mensual colegio", "Matrícula Universidad", "Compra útiles escolares",
        "Curso online Udemy", "Mensualidad instituto de idiomas", "Compra de libros de texto"
    ]
}

# Mapear categorías en inglés a las 8 oficiales en español
mapeo_categorias = {
    'Food & Drink': 'alimentacion',
    'Travel': 'transporte',
    'Health & Fitness': 'salud',
    'Rent': 'vivienda',
    'Utilities': 'servicios',
    'Entertainment': 'entretenimiento',
    'Shopping': 'otros',
    'Other': 'otros',
    'Salary': 'otros',
    'Investment': 'otros',
    'Education': 'educacion'
}

def traducir_dataset(input_path, output_path):
    print(f"Cargando dataset original desde {input_path}...")
    df = pd.read_csv(input_path)
    
    # Semilla para reproductibilidad
    random.seed(42)
    
    nuevas_descripciones = []
    nuevas_categorias = []
    
    for idx, row in df.iterrows():
        cat_original = row['Category']
        
        # Introducir categoría de 'Education' de forma aleatoria en el 15% de los registros 'Other' o 'Shopping'
        # para asegurar que tengamos cobertura de las 8 categorías del caso.
        if cat_original in ['Other', 'Shopping'] and random.random() < 0.15:
            cat_original = 'Education'
            
        # Elegir descripción aleatoria de la lista en español
        desc_list = descripciones_por_categoria.get(cat_original, descripciones_por_categoria['Other'])
        nueva_desc = random.choice(desc_list)
        
        # Mapear la categoría a español
        nueva_cat = mapeo_categorias.get(cat_original, 'otros')
        
        nuevas_descripciones.append(nueva_desc)
        nuevas_categorias.append(nueva_cat)
        
    # Actualizar columnas
    df['Transaction Description'] = nuevas_descripciones
    df['Category'] = nuevas_categorias
    
    # Renombrar columnas al formato del sistema español
    df = df.rename(columns={
        'Amount': 'monto',
        'Date': 'fecha',
        'Category': 'categoria'
    })
    
    # Guardar nuevo dataset
    df.to_csv(output_path, index=False, encoding='utf-8')
    print(f"Dataset traducido con exito. Guardado en: {output_path}")
    print(df[['fecha', 'Transaction Description', 'categoria', 'monto']].head(10))

if __name__ == "__main__":
    input_file = "../data/raw/Personal_Finance_Dataset.csv"
    output_file = "../data/raw/Personal_Finance_Dataset_ES.csv"
    traducir_dataset(input_file, output_file)
