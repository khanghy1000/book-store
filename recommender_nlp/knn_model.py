import os
import warnings

import joblib
import pandas as pd
import spacy
from dotenv import load_dotenv
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.neighbors import NearestNeighbors
from sqlalchemy import create_engine

load_dotenv()
warnings.filterwarnings("ignore")


def get_data():
    POSTGRES_ADDRESS = os.getenv("POSTGRES_ADDRESS")
    POSTGRES_PORT = os.getenv("POSTGRES_PORT")
    POSTGRES_USERNAME = os.getenv("POSTGRES_USERNAME")
    POSTGRES_PASSWORD = os.getenv("POSTGRES_PASSWORD")
    POSTGRES_DBNAME = os.getenv("POSTGRES_DBNAME")

    postgres_str = ('postgresql://{username}:{password}@{ipaddress}:{port}/{dbname}'
                    .format(username=POSTGRES_USERNAME,
                            password=POSTGRES_PASSWORD,
                            ipaddress=POSTGRES_ADDRESS,
                            port=POSTGRES_PORT,
                            dbname=POSTGRES_DBNAME))

    cnx = create_engine(postgres_str)

    data = pd.read_sql_query("SELECT * FROM get_book_data", cnx)
    data.fillna("missing", inplace=True)
    return data


class KnnModel:
    def __init__(self):
        self.data = get_data()
        self.nlp = spacy.blank("vi")
        self.cv = CountVectorizer()
        self.model = NearestNeighbors(metric='cosine', algorithm='brute')

    def clean_text(self, text):
        doc = self.nlp(text.lower())
        tokens = [token.text for token in doc if not token.is_stop and not token.is_punct]
        return " ".join(tokens)

    def train_model(self, model_path):
        self.data["clean_text"] = (
                self.data["book_title"] + " " +
                self.data["publisher_name"] + " " +
                self.data["category_1"] + " " +
                self.data["category_2"] + " " +
                self.data["category_3"] + " " +
                self.data["author_1"] + " " +
                self.data["author_2"] + " " +
                self.data["short_description"] + " " +
                self.data["full_description"] + " "

        )
        self.nlp.Defaults.stop_words.add("sách")
        self.data["clean_text"] = self.data["clean_text"].apply(self.clean_text)

        clean_text_vector = self.cv.fit_transform(self.data['clean_text']).toarray()
        self.model.fit(clean_text_vector)

        joblib.dump(self.model, model_path)

    def load_model(self, model_path):
        self.model = joblib.load(model_path)

    def recommend(self, text, k=5):
        cleaned_text = self.cv.transform([self.clean_text(text)]).toarray()
        distances, indices = self.model.kneighbors(cleaned_text, n_neighbors=k)
        return self.data.iloc[indices[0]][["id", "book_title"]].to_dict(orient="records")
