import sqlite3
import logger
import config
import json

module_name = logger.get_file_name()


class MyCursor(sqlite3.Cursor):
    def __init__(self, connection):
        super(MyCursor, self).__init__(connection)
        self.connected = 0

    def __enter__(self):
        return self

    def __exit__(self, ex_type, ex_value, ex_traceback):
        if ex_type is not None:
            logger.log(logger.get_file_name(), str(ex_value))
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
            logger.log(logger.get_file_name(), f'table {table_name} deleted')

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
            friend_requests_sent text 
            )
            """
            cursor.execute(query)
            logger.log(logger.get_file_name(), f'table {self.users_table_name} just created')

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
                logger.log(logger.get_file_name(), f"new user {username} created")
                ans = {'status': 'OK', 'username': username, 'time': None}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(logger.get_file_name(), f"user {username} already exist")
            ans = {'status': 'User with this username already exists', 'username': username, 'time': None}
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
            logger.log(logger.get_file_name(), f"there is no such user {username}")
            ans = {'status': 'No such user', 'username': username, 'time': None}
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
            logger.log(logger.get_file_name(), f"there is no such user {username}")

            ans = {'status': 'No such user', 'username': username, 'time': None}
            ans = json.dumps(ans)
            return ans

    def get_friend_request(self, username, friend):
        print(32423432423423432432424)
        if self.is_user_exists(username):
            if self.is_user_exists(friend):
                with self.run_cursor() as cursor:
                    print(000000000000000000000000000000000)

                    query = f"""
                        SELECT friend_requests_sent FROM {self.users_table_name} WHERE username = "{username}"
                    """

                    cursor.execute(query)
                    print("Sefa bepis")
                    requests_list = cursor.fetchone()[0]
                    if requests_list:
                        print("Kekekek")
                        requests_list = requests_list.split(',')
                        for friend_request in requests_list:
                            if friend == friend_request:
                                ans = {'status': f'Friend request to {friend} by {username} already sent',
                                       'username': username, 'friend_name': friend}
                                ans = json.dumps(ans)
                                return ans
                    else:
                        print("SEVA DAUN")
                        requests_list = []
                    requests_list.append(friend)
                    new_requests_list = ','.join(requests_list)
                    print(new_requests_list)

                    query = f"""
                        UPDATE {self.users_table_name} SET friend_requests_sent = "{new_requests_list}" WHERE username = "{username}"
                    """
                    print(query)

                    cursor.execute(query)
                    print("Vonni soset")

                    query = f"""
                        SELECT friend_requests_got FROM {self.users_table_name} WHERE username = "{friend}"
                    """

                    cursor.execute(query)
                    print("DFSDFDSFSDFS")
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

                    logger.log(logger.get_file_name(),
                               f"Friend request to {friend} by {username} successfully sent")
                    return ans
            else:
                logger.log(logger.get_file_name(), f"there is no such user {friend}")

                ans = {'status': 'No such user', 'username': friend, 'time': None}
                ans = json.dumps(ans)
                return ans
        else:
            logger.log(logger.get_file_name(), f"there is no such user {username}")

            ans = {'status': 'No such user', 'username': username, 'time': None}
            ans = json.dumps(ans)
            return ans


if __name__ == '__main__':
    req = UsersDb()
    req.create_users_db()
