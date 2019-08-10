import sys
from datetime import datetime
import os.path
import os
import config


def get_file_name():
    return str(os.path.realpath(__file__).replace("\\", "/").split("/")[-1])


def log(module_name, log_line):
    filename = config.logs_file
    try:
        with open("../" + filename, "a") as log_file:
            log_file.write(module_name + " " + log_line + "\n")
    except Exception as err:
        sys.stderr.write("!!!!!!!!!!!!!!!! Cannot open log file " + filename + ": " + str(err) + "\n")
        sys.stderr.flush()
