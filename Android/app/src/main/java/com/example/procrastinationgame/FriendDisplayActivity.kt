package com.example.procrastinationgame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.procrastinationgame.AnimalAdapter
import com.example.procrastinationgame.R
import com.vk.sdk.VKSdk
import kotlinx.android.synthetic.main.activity_friend_display.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.Spliterators.iterator
import kotlin.collections.ArrayList

class FriendDisplayActivity : AppCompatActivity() {
    val name = this::class.java.simpleName

    var newJsonObj = JSONObject(
        CallAPI().execute(
            "http://192.168.212.122:25000/friends",
            "{token: ${VKSdk.getAccessToken().accessToken}}"
        ).toString()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_display)
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
