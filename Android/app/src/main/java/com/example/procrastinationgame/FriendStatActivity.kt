package com.example.procrastinationgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_friend_stat.*
import org.json.JSONObject
import android.text.method.ScrollingMovementMethod



class FriendStatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_stat)
        val id = intent.getStringExtra("USER_ID")
        val strobj = intent.getStringExtra("STRING_OBJ")
        var obj = JSONObject(strobj)
        var users = obj["content"] as JSONObject
        var user = users[id] as JSONObject
        var name = user["name"] as String
        var stats = user["stats"] as JSONObject
        var displayStr = ""
        setTitle(name)
        siteview.text = "Stats: " + stats.toString()
        siteview.setMovementMethod(ScrollingMovementMethod())

    }
}
