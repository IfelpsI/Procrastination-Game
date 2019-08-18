package com.example.procrastinationgame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_friend_stat.*
import org.json.JSONObject


class FriendStatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_display)
        val dummy_name = this::class.java.simpleName

        val intent = getIntent()

        var stats = JSONObject(intent.getStringExtra("STATS"))
        var keys = intent.getStringArrayListExtra("KEYS")
        var name = intent.getStringExtra("NAME")
        Log.e(dummy_name, "kek2 $stats \n $keys")
        stats_name.text = name
        rv_animal_list2.layoutManager = LinearLayoutManager(this)
        rv_animal_list2.adapter = AnimalAdapter(keys, this, stats)
    }
}