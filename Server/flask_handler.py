from flask import Flask, request
import logger
import user_db_manip as us_man
import json
app = Flask(__name__)

us_db = us_man.UsersDb()


smth_went_wrong = {'status': 'Something went wrong'}
smth_went_wrong = json.dumps(smth_went_wrong)


@app.route('/')
def hello_world():
    try:
        logger.log(logger.get_file_name(), "Handler got empty request")
        ans = {'status': 'Got empty request', 'username': '', 'time': None}
        ans = json.dumps(ans)
        return ans
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))
        return smth_went_wrong


@app.route('/make_user')
def make_user():
    try:
        name = request.args.get('username')
        return us_db.create_user(name)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))
        return smth_went_wrong


@app.route('/get_user_stats')
def get_user_stats():
    try:
        name = request.args.get('username')
        return us_db.get_stat_from_user(name)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))
        return smth_went_wrong


@app.route('/update_user_stats')
def update_user_stats():
    try:
        name = request.args.get('username')
        time = request.args.get('time')
        return us_db.update_stat_user(name, time)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))
        return smth_went_wrong


@app.route('/friend_request')
def friend_request():
    try:
        name = request.args.get('username')
        friend = request.args.get('friend')
        return us_db.get_friend_request(name, friend)
    except Exception as err:
        logger.log(logger.get_file_name(), str(err))
        return smth_went_wrong


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=25000, debug=True)