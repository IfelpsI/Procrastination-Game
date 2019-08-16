from flask import Flask, request
import logger
import user_db_manip as us_man
import json
app = Flask(__name__)

us_db = us_man.UsersDb()


smth_went_wrong = {'status': 'Something went wrong'}
smth_went_wrong = json.dumps(smth_went_wrong)

module_name = 'flask_handler.py'


@app.route('/')
def hello_world():
    try:
        logger.log(module_name, "Handler got empty request")
        ans = {'status': 'Got empty request'}
        ans = json.dumps(ans)
        return ans
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/send_token/<query>')
def send_token(query):
    try:
        query = json.loads(query)
        token = query['token']
        us_db.create_vk_user(token)
        us_db.add_vk_friends(token)

        ans = {'status': 'OK'}
        ans = json.dumps(ans)
        return ans
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/send_stats/<query>')
def send_stats(query):
    try:
        query = json.loads(query)
        for key, value in query.items():
            if key == 'token':
                token = query[key]
            else:
                id = key
                apps_list = query[id]['unlock_screen']
        us_db.get_stats_from_app(token, id, apps_list)
        ans = {'status': 'OK'}
        ans = json.dumps(ans)
        return ans
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/friends/<query>')
def get_friends(query):
    try:
        query = json.loads(query)
        token = query['token']
        return us_db.search_for_vk_friends(token)
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong

#
# @app.route('/friend_stat/<query>')
# def get_friend_stat


@app.route('/make_user')
def make_user():
    try:
        name = request.args.get('username')
        return us_db.create_user(name)
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/get_user_stats')
def get_user_stats():
    try:
        name = request.args.get('username')
        return us_db.get_stat_from_user(name)
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/update_user_stats')
def update_user_stats():
    try:
        name = request.args.get('username')
        time = request.args.get('time')
        return us_db.update_stat_user(name, time)
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/friend_request')
def friend_request():
    try:
        name = request.args.get('username')
        friend = request.args.get('friend')
        return us_db.get_friend_request(name, friend)
    except Exception as err:
        logger.log(module_name, str(err))
        return smth_went_wrong


@app.route('/set_vk_id')
def set_vk_id():
    try:
        name = request.args.get('username')
        vk_id = request.args.get('vk_id')
        return us_db.set_user_vk_id(name, vk_id)
    except Exception as err:
        logger.log(module_name, str(err))


@app.route('/get_user_vk_friends')
def get_user_vk_friends():
    try:
        name = request.args.get('username')
        return us_db.search_for_vk_friends(name)
    except Exception as err:
        logger.log(module_name, str(err))


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=25000, debug=True)