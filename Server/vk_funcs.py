import vk_api

module_name = 'vk_funcs.py'


class VkUser:
    def __init__(self, token):
        self.token = token
        self.vk = vk_api.VkApi(token=self.token)

    def get_user_info(self):
        params = {'v': '5.101'}
        user_info = self.vk.method('users.get', params)
        return user_info

    def get_friend_list(self):
        params = {'v': '5.101', 'order': 'hints', 'fields': 'domain'}
        friends = self.vk.method('friends.get', params)
        return friends


#lesha = VkUser('')
#print(lesha.get_friend_list())


