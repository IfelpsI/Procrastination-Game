package com.example.procrastinationgame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.vk.sdk.VKSdk
import kotlinx.android.synthetic.main.activity_friend_display.*
import org.json.JSONObject

class FriendDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_display)
        val name = this::class.java.simpleName
        var response =
            CallAPI().execute(
                "http://192.168.212.122:25000/friends/",
                "{\"token\": \"${VKSdk.getAccessToken().accessToken}\"}"
            ).get()

        var newJsonObj = JSONObject(response)
        val users = newJsonObj["content"] as JSONObject
        val userIds: ArrayList<String> = ArrayList()
        for (key in users.keys()) {
            userIds.add(key)
        }
        Log.e(name, "kek2 $userIds")
        rv_animal_list.layoutManager = LinearLayoutManager(this)
        rv_animal_list.adapter = AnimalAdapter(userIds, this, newJsonObj)
    }
}
