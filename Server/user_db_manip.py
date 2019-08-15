import sqlite3
import logger
import config
import json
import vk_api

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
            username text not null,
            time_on_phone integer,
            friends text,
            friend_requests_got text,
            friend_requests_sent text,
            vk_id text,
            vk_friends text 
            )
            """
            cursor.execute(query)
            logger.log(module_name, f'table {self.users_table_name} just created')

    def is_user_exists(self, username):
        with self.run_cursor() as cursor:
            query = f"""
                SELECT id FROM {self.users_table_name} WHERE username = "{username}"
            """
            cursor.execute(query)
            if cursor.fetchone() is None:
                return False
            else:
                return True

    def create_user(self, username):
        if not self.is_user_exists(username):
            with self.run_cursor() as cursor:
                query = f"""
                    INSERT INTO {self.users_table_name} (username, time_on_phone) VALUES ("{username}", 0)
                """
                cursor.execute(query)
                logger.log(module_name, f"new user {username} created")
                ans = {'status': 'OK', 'username': username, 'time': None}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"user {username} already exist")
            ans = {'status': 'User with this username already exists', 'username': username}
            ans = json.dumps(ans)
            return ans

    def get_stat_from_user(self, username):
        if self.is_user_exists(username):
            with self.run_cursor() as cursor:
                query = f"""
                    SELECT time_on_phone FROM {self.users_table_name} WHERE username = "{username}"
                """
                cursor.execute(query)
                time = cursor.fetchone()[0]
                ans = {'status': 'OK', 'username': username, 'time': time}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {username}")
            ans = {'status': 'No such user', 'username': username}
            ans = json.dumps(ans)
            return ans

    def update_stat_user(self, username, time):
        if self.is_user_exists(username):
            with self.run_cursor() as cursor:

                query = f"""
                    SELECT time_on_phone FROM {self.users_table_name} WHERE username = "{username}"
                """

                cursor.execute(query)
                prev_time = int(cursor.fetchone()[0])
                time = int(time) + prev_time

                query = f"""
                    UPDATE {self.users_table_name} SET time_on_phone = {time} WHERE username = "{username}"
                """

                cursor.execute(query)

                ans = {'status': 'OK', 'username': username, 'time': time}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {username}")

            ans = {'status': 'No such user', 'username': username}
            ans = json.dumps(ans)
            return ans

    def get_friend_request(self, username, friend):
        if self.is_user_exists(username):
            if self.is_user_exists(friend):
                with self.run_cursor() as cursor:

                    query = f"""
                        SELECT friend_requests_sent FROM {self.users_table_name} WHERE username = "{username}"
                    """

                    cursor.execute(query)
                    requests_list = cursor.fetchone()[0]
                    if requests_list:
                        requests_list = requests_list.split(',')
                        for friend_request in requests_list:
                            if friend == friend_request:
                                ans = {'status': f'Friend request to {friend} by {username} already sent',
                                       'username': username, 'friend_name': friend}
                                ans = json.dumps(ans)
                                return ans
                    else:
                        requests_list = []
                    requests_list.append(friend)
                    new_requests_list = ','.join(requests_list)

                    query = f"""
                        UPDATE {self.users_table_name} SET friend_requests_sent = "{new_requests_list}" WHERE username = "{username}"
                    """

                    cursor.execute(query)

                    query = f"""
                        SELECT friend_requests_got FROM {self.users_table_name} WHERE username = "{friend}"
                    """

                    cursor.execute(query)
                    got_requests_list = cursor.fetchone()[0]
                    if got_requests_list:
                        got_requests_list = got_requests_list.split(',')
                    else:
                        got_requests_list = []
                    got_requests_list.append(username)
                    got_requests_list = ','.join(got_requests_list)

                    query = f"""
                        UPDATE {self.users_table_name} SET friend_requests_got = "{got_requests_list}"
                        WHERE username = "{friend}"
                    """

                    cursor.execute(query)

                    ans = {'status': f'Friend request to {friend} by {username} successfully sent',
                           'username': username, 'friend_name': friend}
                    ans = json.dumps(ans)

                    logger.log(module_name,
                               f"Friend request to {friend} by {username} successfully sent")
                    return ans
            else:
                logger.log(module_name, f"there is no such user {friend}")

                ans = {'status': 'No such user', 'username': friend}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {username}")

            ans = {'status': 'No such user', 'username': username}
            ans = json.dumps(ans)
            return ans

    def set_user_vk_id(self, username, vk_id):
        if self.is_user_exists(username):
            with self.run_cursor() as cursor:

                query = f"""
                    UPDATE {self.users_table_name} SET vk_id = "{vk_id}" WHERE username = "{username}"
                """

                cursor.execute(query)

                ans = {'status': f'vk_id added to user {username}', 'username': username}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {username}")

            ans = {'status': 'No such user', 'username': username}
            ans = json.dumps(ans)
            return ans

    def search_for_vk_friends(self, username):
        if self.is_user_exists(username):
            with self.run_cursor() as cursor:

                query = f"""
                    SELECT vk_friends FROM {self.users_table_name} WHERE username = "{username}"
                """

                cursor.execute(query)
                vk_friends_list = cursor.fetchone()[0]
                if vk_friends_list:
                    vk_friends_list = vk_friends_list.split(',')
                else:

                    ans = {'status': 'User has no vk friends', 'username': username}
                    ans = json.dumps(ans)
                    return ans
                vk_friends_username = []
                for friend_id in vk_friends_list:

                    query = f"""
                        SELECT username FROM {self.users_table_name} WHERE vk_id = "{friend_id}"
                    """

                    cursor.execute(query)
                    friend = cursor.fetchone()[0]
                    if friend:
                        vk_friends_username.append(friend)

                ans = {'status': 'List of vk friends', 'username': username, 'list_of_vk_friends': vk_friends_username}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(module_name, f"there is no such user {username}")

            ans = {'status': 'No such user', 'username': username}
            ans = json.dumps(ans)
            return ans


if __name__ == '__main__':
    req = UsersDb()
    req.create_users_db()
