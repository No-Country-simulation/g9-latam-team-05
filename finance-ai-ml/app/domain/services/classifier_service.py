from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import make_pipeline

class TransactionClassifierService:
    def __init__(self):
        # Datos de entrenamiento sintéticos
        X_train = [
            "Supermercado Plaza", "Exito San Martin", "Jumbo Cencosud", "Tienda D1", "Carulla Express",
            "Gasolinera Repsol", "Terpel Estacion", "Uber Trip", "Didi Ride", "Peaje Cundinamarca",
            "Restaurante El Corral", "Mc Donalds", "Starbucks Coffee", "Crepes and Waffles",
            "Gimnasio Bodytech", "Drogueria Cruz Verde", "Farmatodo Colombia"
        ]
        y_train = [
            "Alimentación", "Alimentación", "Alimentación", "Alimentación", "Alimentación",
            "Transporte", "Transporte", "Transporte", "Transporte", "Transporte",
            "Restaurantes", "Restaurantes", "Restaurantes", "Restaurantes",
            "Salud/Bienestar", "Salud/Bienestar", "Salud/Bienestar"
        ]
        
        # Pipeline TF-IDF + Multinomial Naive Bayes
        self.model = make_pipeline(TfidfVectorizer(ngram_range=(1, 2)), MultinomialNB())
        self.model.fit(X_train, y_train)

    def predict(self, texts: list[str]) -> list[str]:
        return self.model.predict(texts).tolist()