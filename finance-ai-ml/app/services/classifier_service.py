from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline

import os
import unicodedata
import pandas as pd


def normalizar_texto(txt: str) -> str:
    """Normaliza texto eliminando acentos, caracteres especiales y convirtiendo a minúsculas."""
    if not txt:
        return ""
    return "".join(
        c for c in unicodedata.normalize('NFD', str(txt))
        if unicodedata.category(c) != 'Mn'
    ).lower().strip()


# Palabras vacías (Stopwords) financieras y conectores que suelen desviar el significado
FINANCIAL_STOPWORDS = {
    'gasto', 'gastos', 'pago', 'pagos', 'compra', 'compras', 'comprar', 'compro',
    'de', 'del', 'la', 'el', 'los', 'las', 'un', 'una', 'unos', 'unas',
    'en', 'por', 'para', 'con', 'mi', 'mis', 'al', 'a', 'y', 'e', 'o', 'u',
    'payment', 'expense', 'expenses', 'purchase', 'bought', 'buy', 'for', 'in', 'at', 'the', 'and', 'to', 'of'
}


class TransactionClassifierService:
    """
    Servicio de clasificación de transacciones con soporte bilingüe (Español / Inglés)
    y modismos regionales de Latinoamérica (Perú, México, Colombia, Argentina, etc.).
    """
    def __init__(self):
        base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
        csv_es = os.path.join(base_dir, "data", "raw", "Personal_Finance_Dataset_ES.csv")
        csv_en = os.path.join(base_dir, "data", "raw", "Personal_Finance_Dataset.csv")

        X_train = []
        y_train = []

        # 1. Cargar dataset en español
        if os.path.exists(csv_es):
            df_es = pd.read_csv(csv_es)
            X_train.extend([normalizar_texto(t) for t in df_es['Transaction Description']])
            y_train.extend([normalizar_texto(c) for c in df_es['categoria']])

        # 2. Cargar y homologar dataset en inglés (si existe)
        if os.path.exists(csv_en):
            df_en = pd.read_csv(csv_en)
            cat_map = {
                'groceries': 'alimentacion', 'food': 'alimentacion', 'dining': 'alimentacion', 'restaurant': 'alimentacion',
                'transportation': 'transporte', 'fuel': 'transporte', 'travel': 'transporte', 'gas': 'transporte',
                'utilities': 'servicios', 'bills': 'servicios', 'utility': 'servicios',
                'entertainment': 'entretenimiento', 'leisure': 'entretenimiento', 'games': 'entretenimiento',
                'healthcare': 'salud', 'medical': 'salud', 'health': 'salud', 'pharmacy': 'salud',
                'education': 'educacion', 'tuition': 'educacion', 'school': 'educacion',
                'housing': 'vivienda', 'rent': 'vivienda', 'home': 'vivienda',
                'other': 'otros', 'salary': 'ingresos', 'income': 'ingresos'
            }
            for _, row in df_en.iterrows():
                desc = normalizar_texto(row.get('Transaction Description', row.get('Description', '')))
                cat = normalizar_texto(row.get('Category', row.get('categoria', 'otros')))
                cat_homologada = cat_map.get(cat, 'otros')
                if desc:
                    X_train.append(desc)
                    y_train.append(cat_homologada)

        # 3. Data Augmentation bilingüe y regional (Viajes, Regalos, Educación, Comida regional, etc.)
        regional_dataset = [
            # Transporte / Viajes
            ('viaje a cusco arequipa lima trujillo', 'transporte'),
            ('viaje a cusco vacaciones turismo viaje', 'transporte'),
            ('gasto en viaje a cusco', 'transporte'),
            ('vuelo a cusco latam sky airlines', 'transporte'),
            ('pasaje de avion vuelo pasajes aereos', 'transporte'),
            ('pasaje de bus interprovincial cruz del sur civa movil bus', 'transporte'),
            ('peaje autopista via expresa panamericana peajes', 'transporte'),
            ('uber didi taxi cabify indrive aeropuerto transporte', 'transporte'),
            ('pasaje de combi micro colectivo tren metro metropolitano', 'transporte'),
            ('gasolina grifo primax repsol petroperu pecsa magna ypf', 'transporte'),
            ('gas station fuel petrol charging station travel flight', 'transporte'),

            # Otros (Compras personales, regalos, ropa, calzado, mascotas)
            ('regalo para mi novia nobia enamorada pareja esposa mujer', 'otros'),
            ('regalo de cumpleanos aniversario detalle obsequio presente', 'otros'),
            ('compra de regalo para mi nobia eunice', 'otros'),
            ('compra de regalo para mi novia', 'otros'),
            ('flores ramo floreria rosas chocolates arreglo floral', 'otros'),
            ('ropa en zara falabella ripley h&m oechsle tiendas', 'otros'),
            ('zapatillas nike adidas puma marathon calzado', 'otros'),
            ('pantalon camisa polo casaca chompa vestimenta vestido', 'otros'),
            ('perfume colonia reloj joyeria joya accesorios', 'otros'),
            ('ferreteria sodimac promart maestro articulos herramientas casa', 'otros'),
            ('veterinaria consulta y comida de perro gato mascota', 'otros'),
            ('fotocopias impresiones libreria utiles de oficina escritorio', 'otros'),
            ('shopping gifts clothes shoes jewelry cosmetics', 'otros'),

            # Educación
            ('pago de mensualidad de la universidad', 'educacion'),
            ('gasto en pago de mensualidad de la universidad', 'educacion'),
            ('mensualidad universidad san martin ucv upc pcp ulima udep', 'educacion'),
            ('pension colegio escuela secundaria primaria matricula', 'educacion'),
            ('matricula universidad posgrado maestria diplomado', 'educacion'),
            ('curso online udemy platzi coursera bootcamp libros copias', 'educacion'),
            ('tuition fee college university master class student books', 'educacion'),

            # Alimentación (Perú / México / Internacional)
            ('palta para el almuerzo comida', 'alimentacion'),
            ('compra de palta para el almuerzo', 'alimentacion'),
            ('palta fuerte palta hass verduleria aguacate', 'alimentacion'),
            ('menu del dia almuerzo cena chifa ceviche', 'alimentacion'),
            ('compra de menu del dia', 'alimentacion'),
            ('supermercado metro plaza vea tottus vivanda wong', 'alimentacion'),
            ('mercado verduras frutas carnes abarrotes viveres pollo', 'alimentacion'),
            ('restaurante cena almuerzo fast food burger kfc starbucks', 'alimentacion'),
            ('walmart groceries grocery shopping super supermarket', 'alimentacion'),

            # Servicios
            ('recibo de luz enel luz del sur cfe edenor', 'servicios'),
            ('recibo de agua sedapal comapa aysa', 'servicios'),
            ('recibo de internet fibra optica claro movistar entel win', 'servicios'),
            ('plan movil celular recarga telefono', 'servicios'),
            ('electric bill water internet utility service payment', 'servicios'),

            # Entretenimiento
            ('entradas para el cine cineplanet cinemark pelicula entradas', 'entretenimiento'),
            ('suscripcion netflix spotify disney max streaming', 'entretenimiento'),
            ('salida nocturna bar discoteca fiesta cerveza tragos fiesta', 'entretenimiento'),
            ('videojuegos steam playstation nintendo xbox', 'entretenimiento'),

            # Vivienda
            ('alquiler de departamento renta depa piso habitacion', 'vivienda'),
            ('mantenimiento de edificio cuota condominio depa', 'vivienda'),
            ('apartment rent leasing mortgage payment house', 'vivienda'),

            # Ingresos
            ('abono de sueldo nomina salario quincena', 'ingresos'),
            ('transferencia recibida pago de cliente honorarios', 'ingresos'),
            ('salary paycheck direct deposit income payment deposit', 'ingresos')
        ]

        for desc, cat in regional_dataset:
            X_train.append(normalizar_texto(desc))
            y_train.append(normalizar_texto(cat))

        # 4. Pipeline NLP: TF-IDF con sublinear scaling + Clasificador Lineal con balanceo de clases
        vectorizer = TfidfVectorizer(
            ngram_range=(1, 2),
            sublinear_tf=True,
            stop_words=list(FINANCIAL_STOPWORDS),
            max_features=8000
        )

        self.model = make_pipeline(
            vectorizer,
            LogisticRegression(C=3.5, max_iter=600, class_weight='balanced', random_state=42)
        )
        self.model.fit(X_train, y_train)

    def predict(self, texts: list[str]) -> list[str]:
        """Predice la categoría para una lista de descripciones en lenguaje natural."""
        normalized_texts = [normalizar_texto(t) for t in texts]
        predictions = self.model.predict(normalized_texts)
        
        categoria_format = {
            'alimentacion': 'Alimentación',
            'transporte': 'Transporte',
            'salud': 'Salud',
            'vivienda': 'Vivienda',
            'educacion': 'Educación',
            'entretenimiento': 'Entretenimiento',
            'servicios': 'Servicios',
            'ingresos': 'Ingresos',
            'otros': 'Otros'
        }
        
        return [categoria_format.get(p.lower(), p.capitalize()) for p in predictions]
