package com.example.procrastinationgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_friend_stat.*

class FriendStatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_stat)
        setTitle("FRIEND NAME");
        var name = "Bob"
        siteview.text = "name: " + name
    }
}
