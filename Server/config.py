# -*- coding: utf-8-*-
import json
import os

cur_path = os.path.dirname(os.path.abspath(__file__))


def load_config(filename):
    configfile = open(filename, "r", encoding="utf-8")
    jsonstring = configfile.read()
    res = json.loads(jsonstring)
    configfile.close()
    return res


config_file_names = cur_path + "/configs/config_file_names.json"
logs_file = load_config(config_file_names)["logs_file"]
db_file = load_config(config_file_names)["db_file"]