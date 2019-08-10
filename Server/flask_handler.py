from flask import Flask
import logger
import user_db_manip as us_man
app = Flask(__name__)

us_db = us_man.UsersDb()


@app.route('/')
def hello_world():
    try:
        logger.log(logger.get_file_name(), "Handler got empty request")
        return ""
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


@app.route('/make_user/<name>')
def make_user(name):
    try:
        us_db.create_user(name)
        return ""
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


@app.route('/get_user_stats/<name>')
def get_user_stats(name):
    try:
        return str(us_db.get_stat_from_user(name))
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


@app.route('/update_user_stats/<name>/<time>')
def update_user_stats(name, time):
    try:
        return str(us_db.update_stat_user(name, time))
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))



if __name__ == '__main__':
    app.run(host="0.0.0.0", port=25000, debug=True)