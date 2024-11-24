import warnings

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from knn_model import KnnModel

warnings.filterwarnings("ignore")

knn_model = KnnModel()
knn_model.train_model("./models/knn.model")
knn_model.load_model("./models/knn.model")

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/recommend")
def rec(keyword: str, k: int = 5):
    data = knn_model.recommend(keyword, k)
    return data
