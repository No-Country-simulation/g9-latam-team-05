from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import make_pipeline

import os
import pandas as pd

class TransactionClassifierService:
    def __init__(self):
        # Cargar dataset traducido
        base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))
        csv_path = os.path.join(base_dir, "data", "raw", "Personal_Finance_Dataset_ES.csv")
        
        import unicodedata
        def normalizar(txt: str) -> str:
            return "".join(
                c for c in unicodedata.normalize('NFD', str(txt))
                if unicodedata.category(c) != 'Mn'
            ).lower()

        if os.path.exists(csv_path):
            df = pd.read_csv(csv_path)
            X_train = [normalizar(t) for t in df['Transaction Description']]
            y_train = [normalizar(c) for c in df['categoria']]
        else:
            # Fallback por seguridad
            X_train = ["supermercado", "gasolinera", "clinica", "alquiler"]
            y_train = ["alimentacion", "transporte", "salud", "vivienda"]
        
        # Pipeline TF-IDF + Multinomial Naive Bayes
        self.model = make_pipeline(TfidfVectorizer(ngram_range=(1, 2)), MultinomialNB())
        self.model.fit(X_train, y_train)

    def predict(self, texts: list[str]) -> list[str]:
        import unicodedata
        def normalizar(txt: str) -> str:
            return "".join(
                c for c in unicodedata.normalize('NFD', str(txt))
                if unicodedata.category(c) != 'Mn'
            ).lower()
        
        normalized_texts = [normalizar(t) for t in texts]
        return self.model.predict(normalized_texts).tolist()