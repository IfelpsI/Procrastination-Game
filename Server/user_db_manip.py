import sqlite3
import logger
import config
import json
import vk_funcs

module_name = 'user_db_manip.py'


class MyCursor(sqlite3.Cursor):
    def __init__(self, connection):
        super(MyCursor, self).__init__(connection)
        self.connected = 0

    def __enter__(self):
        return self

    def __exit__(self, ex_type, ex_value, ex_traceback):
        if ex_type is not None:
            logger.log(module_name, str(ex_value))
        self.connected -= 1
        if not self.connected:
            self.connection.commit()
            self.close()
            self.connection.close()


class UsersDb:
    def __init__(self):
        self.database_name = config.db_file
        self.cursor = None
        self.users_table_name = "users"

    def run_cursor(self):
        if self.cursor is None or not self.cursor.connected:
            self.cursor = MyCursor(sqlite3.connect(self.database_name, isolation_level=None))
        self.cursor.connected += 1
        return self.cursor

    def del_table(self, table_name):
        with self.run_cursor() as cursor:
            query = f'DROP TABLE IF EXISTS {table_name}'
            cursor.execute(query)
            logger.log(module_name, f'table {table_name} deleted')

    def create_users_db(self):
        with self.run_cursor() as cursor:
            self.del_table(self.users_table_name)

            query = f"""
                CREATE TABLE {self.users_table_name}
            (
            id integer not null primary key autoincrement,
            name text,
            surname text,
            time_on_phone integer,
            friends text,
            friend_requests_got text,
            friend_requests_sent text,
            vk_id text,
            vk_friends text,
            vk_token text,
            vk_photo text,
            apps_time integer 
            )
            """
            cursor.execute(query)
            logger.log(module_name, f'table {self.users_table_name} just created')

    def is_user_exists(self, vk_id):
        with self.run_cursor() as cursor:
            query = f"""
                SELECT id FROM {self.users_table_name} WHERE vk_id = "{vk_id}"
            """
            cursor.execute(query)
            if cursor.fetchone() is None:
                return False
            else:
                return True

    def create_vk_user(self, vk_token):
        user = vk_funcs.VkUser(vk_token)
        user_vk_info = user.get_user_info()
        user_vk_id = user_vk_info[0]['id']
        if not self.is_user_exists(user_vk_id):
            user_name = user_vk_info[0]['first_name']
            user_surname = user_vk_info[0]['last_name']
            vk_photo = user_vk_info[0]['photo']
            base_app_stats = {'apps': 'no_apps'}
            base_app_stats = str(base_app_stats)
            with self.run_cursor() as cursor:
                query = f"""
                    INSERT INTO {self.users_table_name} 
                    (vk_id, time_on_phone, name, surname, apps_time, vk_token, vk_photo)
                     VALUES 
                     ("{user_vk_id}", 0, "{user_name}", "{user_surname}", "{base_app_stats}", "{vk_token}", "{vk_photo}")
                """

                cursor.execute(query)
                logger.log(module_name, f"new user {user_vk_id} created")
                ans = {'status': 'OK'}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"user {user_vk_id} already exists")
            ans = {'status': f"user {user_vk_id} already exists"}
            ans = json.dumps(ans)
            return ans

    def add_vk_friends(self, vk_token):
        user = vk_funcs.VkUser(vk_token)
        user_vk_info = user.get_user_info()
        user_vk_id = user_vk_info[0]['id']
        if self.is_user_exists(user_vk_id):
            user_vk_friends_info = user.get_friend_list()
            friends_in_app = []
            for vk_friend in user_vk_friends_info['items']:
                with self.run_cursor() as cursor:

                    query = f"""
                        SELECT id FROM {self.users_table_name} WHERE vk_id = "{vk_friend['id']}"
                    """

                    cursor.execute(query)
                    friend_id = cursor.fetchone()
                if friend_id is not None:
                    friends_in_app.append(str(friend_id[0]))
                    self.add_friend_back(vk_friend['id'], user_vk_id)
            vk_friend_list = ','.join(friends_in_app)
            if vk_friend_list != "":
                with self.run_cursor() as cursor:

                    query = f"""
                        UPDATE {self.users_table_name} SET vk_friends = "{vk_friend_list}" WHERE vk_id = "{user_vk_id}"
                    """

                    cursor.execute(query)

            ans = {'status': 'OK'}
            ans = json.dumps(ans)
            return ans
        else:
            logger.log(module_name, f"there is no such user {user_vk_id}")
            ans = {'status': f'No such user {user_vk_id}'}
            ans = json.dumps(ans)
            return ans

    def add_friend_back(self, user_vk_id, friend_vk_id):
        with self.run_cursor() as cursor:

            query = f"""
                SELECT id FROM {self.users_table_name} WHERE vk_id = "{friend_vk_id}"
            """

            cursor.execute(query)
            friend_id = str(cursor.fetchone()[0])

            query = f"""
                SELECT vk_friends FROM {self.users_table_name} WHERE vk_id = "{user_vk_id}"
            """

            cursor.execute(query)
            friends_list = []
            friends = cursor.fetchone()[0]
            if friends is not None:
                friends_list = friends.split(',')
            friends_list.append(friend_id)
            friends_list_str = ','.join(friends_list)

            query = f"""
                UPDATE {self.users_table_name} SET vk_friends = "{friends_list_str}" WHERE vk_id = "{user_vk_id}"
            """

            cursor.execute(query)

    def get_stats_from_app(self, vk_token, id, apps_stats):
        user = vk_funcs.VkUser(vk_token)
        user_vk_info = user.get_user_info()
        user_vk_id = user_vk_info[0]['id']
        if self.is_user_exists(user_vk_id):
            phone_time = apps_stats['unlock_screen']
            apps_stats.pop('unlock_screen')
            with self.run_cursor() as cursor:

                query = f"""
                    UPDATE {self.users_table_name} SET time_on_phone = {phone_time}, apps_time = "{str(apps_stats)}"
                    WHERE id = {id} 
                """

                cursor.execute(query)

                ans = {'status': 'OK'}
                ans = json.dumps(ans)
                return ans

        else:
            logger.log(module_name, f"there is no such user {user_vk_id}")
            ans = {'status': f'No such user {user_vk_id}'}
            ans = json.dumps(ans)
            return ans

    def get_stat_from_user(self, vk_token):
        user = vk_funcs.VkUser(vk_token)
        user_vk_info = user.get_user_info()
        user_vk_id = user_vk_info[0]['id']
        if self.is_user_exists(user_vk_id):
            with self.run_cursor() as cursor:

                query = f"""
                    SELECT id, name, surname, apps_time, time_on_phone, vk_photo 
                    FROM {self.users_table_name} WHERE vk_id = "{user_vk_id}"
                """

                cursor.execute(query)
                user_info = cursor.fetchone()
                if user_info:
                    stats = eval(user_info[3])
                    stats['unlock_screen'] = user_info[4]
                    print(user_info[1])
                    vk_user_info = {'name': user_info[1], 'surname': user_info[2], 'photo': user_info[5], 'stats': stats}

            ans = {'status': 'OK', user_info[0]: vk_user_info}
            ans = json.dumps(ans)
            return ans
        else:
            logger.log(module_name, f"there is no such user {user_vk_id}")
            ans = {'status': f'No such user {user_vk_id}'}
            ans = json.dumps(ans)
            return ans

    def update_stat_user(self, vk_token, time):
        if self.is_user_exists(vk_token):
            with self.run_cursor() as cursor:

                query = f"""
                    SELECT time_on_phone FROM {self.users_table_name} WHERE vk_token = "{vk_token}"
                """

                cursor.execute(query)
                prev_time = int(cursor.fetchone()[0])
                time = int(time) + prev_time

                query = f"""
                    UPDATE {self.users_table_name} SET time_on_phone = {time} WHERE vk_token = "{vk_token}"
                """

                cursor.execute(query)

                ans = {'status': 'OK', 'vk_token': vk_token, 'time': time}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {vk_token}")

            ans = {'status': 'No such user', 'vk_token': vk_token}
            ans = json.dumps(ans)
            return ans

    def get_friend_request(self, vk_token, friend):
        if self.is_user_exists(vk_token):
            if self.is_user_exists(friend):
                with self.run_cursor() as cursor:

                    query = f"""
                        SELECT friend_requests_sent FROM {self.users_table_name} WHERE vk_token = "{vk_token}"
                    """

                    cursor.execute(query)
                    requests_list = cursor.fetchone()[0]
                    if requests_list:
                        requests_list = requests_list.split(',')
                        for friend_request in requests_list:
                            if friend == friend_request:
                                ans = {'status': f'Friend request to {friend} by {vk_token} already sent',
                                       'vk_token': vk_token, 'friend_name': friend}
                                ans = json.dumps(ans)
                                return ans
                    else:
                        requests_list = []
                    requests_list.append(friend)
                    new_requests_list = ','.join(requests_list)

                    query = f"""
                        UPDATE {self.users_table_name} SET friend_requests_sent = "{new_requests_list}" WHERE vk_token = "{vk_token}"
                    """

                    cursor.execute(query)

                    query = f"""
                        SELECT friend_requests_got FROM {self.users_table_name} WHERE vk_token = "{friend}"
                    """

                    cursor.execute(query)
                    got_requests_list = cursor.fetchone()[0]
                    if got_requests_list:
                        got_requests_list = got_requests_list.split(',')
                    else:
                        got_requests_list = []
                    got_requests_list.append(vk_token)
                    got_requests_list = ','.join(got_requests_list)

                    query = f"""
                        UPDATE {self.users_table_name} SET friend_requests_got = "{got_requests_list}"
                        WHERE vk_token = "{friend}"
                    """

                    cursor.execute(query)

                    ans = {'status': f'Friend request to {friend} by {vk_token} successfully sent',
                           'vk_token': vk_token, 'friend_name': friend}
                    ans = json.dumps(ans)

                    logger.log(module_name,
                               f"Friend request to {friend} by {vk_token} successfully sent")
                    return ans
            else:
                logger.log(module_name, f"there is no such user {friend}")

                ans = {'status': 'No such user', 'vk_token': friend}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {vk_token}")

            ans = {'status': 'No such user', 'vk_token': vk_token}
            ans = json.dumps(ans)
            return ans

    def set_user_vk_id(self, vk_token, vk_id):
        if self.is_user_exists(vk_token):
            with self.run_cursor() as cursor:

                query = f"""
                    UPDATE {self.users_table_name} SET vk_id = "{vk_id}" WHERE vk_token = "{vk_token}"
                """

                cursor.execute(query)

                ans = {'status': f'vk_id added to user {vk_token}', 'vk_token': vk_token}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {vk_token}")

            ans = {'status': 'No such user', 'vk_token': vk_token}
            ans = json.dumps(ans)
            return ans

    def search_for_vk_friends(self, vk_token):
        user = vk_funcs.VkUser(vk_token)
        user_vk_info = user.get_user_info()
        user_vk_id = user_vk_info[0]['id']
        if self.is_user_exists(user_vk_id):
            with self.run_cursor() as cursor:

                query = f"""
                    SELECT vk_friends FROM {self.users_table_name} WHERE vk_id = "{user_vk_id}"
                """

                cursor.execute(query)
                vk_friends_list = cursor.fetchone()
                if vk_friends_list:
                    vk_friends_list = vk_friends_list[0]
                    vk_friends_list = vk_friends_list.split(',')
                else:

                    ans = {'status': 'User has no vk friends'}
                    ans = json.dumps(ans)
                    return ans
                vk_friends_info = {}
                for friend_id in vk_friends_list:

                    query = f"""
                        SELECT id, name, surname, apps_time, time_on_phone, vk_photo 
                        FROM {self.users_table_name} WHERE id = "{friend_id}"
                    """

                    cursor.execute(query)
                    friend = cursor.fetchone()
                    if friend:
                        stats = eval(friend[3])
                        stats['unlock_screen'] = friend[4]
                        vk_friends_info[str(friend[0])] =\
                            {'name': friend[1], 'surname': friend[2], 'photo': friend[5], 'stats': stats}

                ans = {'status': 'OK', 'content': vk_friends_info}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {user_vk_id}")
            ans = {'status': f'No such user {user_vk_id}'}
            ans = json.dumps(ans)
            return ans


if __name__ == '__main__':
    req = UsersDb()
    req.create_users_db()
