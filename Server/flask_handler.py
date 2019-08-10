from flask import Flask
import logger
import user_db_manip as us_man
import json
app = Flask(__name__)

us_db = us_man.UsersDb()


@app.route('/')
def hello_world():
    try:
        logger.log(logger.get_file_name(), "Handler got empty request")
        ans = {'status': 'Got empty request', 'username': '', 'time': None}
        ans = json.dumps(ans)
        return ans
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


@app.route('/make_user/<query>')
def make_user(query):
    try:
        query = json.loads(query)
        name = query['username']
        return us_db.create_user(name)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


@app.route('/get_user_stats/<query>')
def get_user_stats(query):
    try:
        query = json.loads(query)
        name = query['username']
        return us_db.get_stat_from_user(name)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


@app.route('/update_user_stats/<query>')
def update_user_stats(query):
    try:
        query = json.loads(query)
        name = query['username']
        time = query['time']
        return us_db.update_stat_user(name, time)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=25000, debug=True)