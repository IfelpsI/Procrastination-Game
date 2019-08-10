import sqlite3
import logger
import config


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
            time_on_phone integer 
            );
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
                return True
        else:
            logger.log(logger.get_file_name(), f"user {username} already exist")
            return False

    def get_stat_from_user(self, username):
        if self.is_user_exists(username):
            with self.run_cursor() as cursor:
                query = f"""
                    SELECT time_on_phone FROM {self.users_table_name} WHERE username = "{username}"
                """
                cursor.execute(query)
                return cursor.fetchone()
        else:
            logger.log(logger.get_file_name(), f"there is no such user {username}")
            return "No such user"

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

                return time
        else:
            logger.log(logger.get_file_name(), f"there is no such user {username}")
            return "No such user"


if __name__ == '__main__':
    req = UsersDb()
    req.create_users_db()