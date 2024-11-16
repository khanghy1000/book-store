import warnings
import tensorflow as tf
from fastapi import FastAPI
from libreco.algorithms import TwoTower
from libreco.data import DataInfo

warnings.filterwarnings("ignore")
tf.compat.v1.reset_default_graph()

loaded_data_info = DataInfo.load("models", model_name="two_tower")
loaded_model = TwoTower.load("models", model_name="two_tower", data_info=loaded_data_info)


def convert_np_val(data):
    return {int(key): value.tolist() for key, value in data.items()}


app = FastAPI()


@app.get("/recommend")
def rec(user_id: int, n: int = 6):
    data = loaded_model.recommend_user(user=user_id, n_rec=n)
    return convert_np_val(data)


@app.get("/recommend-knn")
def rec(item_id: int, n: int = 6):
    data = loaded_model.search_knn_items(item=item_id, k=n)
    return convert_np_val(data)