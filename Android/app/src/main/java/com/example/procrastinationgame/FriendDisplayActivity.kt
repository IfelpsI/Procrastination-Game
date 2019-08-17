package com.example.procrastinationgame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.example.procrastinationgame.AnimalAdapter
import com.example.procrastinationgame.R
import kotlinx.android.synthetic.main.activity_friend_display.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.Spliterators.iterator
import kotlin.collections.ArrayList

class FriendDisplayActivity : AppCompatActivity() {

    val animals: ArrayList<String> = ArrayList()
    var newJsonObj = JSONObject("{\"status\": \"ok\", \"content\": {\n" +
            "\"b025-d577-344e-1cb6\": {\"name\": \"Daniel\", \"friends\": [\"3996-d773-4242-ab4a\", \"b025-d577-344e-1cb6\", \"7ad1-4cea-0c37-ef7d\", \"f4e1-f68f-ec70-aa4d\", \"d405-b8c1-bba0-2261\", \"23bd-a865-74f1-6a35\"], \"stats\": {\"com.crossy_road.android\": {\"name\": \"Crossy Road\", \"stats\": {\"lastDay\": \"640\"}}, \"com.twitter.android\": {\"name\": \"Twitter\", \"stats\": {\"lastDay\": \"153\"}}, \"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"916\"}}, \"com.minecraft.android\": {\"name\": \"Minecraft\", \"stats\": {\"lastDay\": \"751\"}}, \"com.instagram.android\": {\"name\": \"Instagram\", \"stats\": {\"lastDay\": \"229\"}}}}, \n" +
            "\"77df-dc4a-7948-088a\": {\"name\": \"Ivan\", \"friends\": [], \"stats\": {\"com.instagram.android\": {\"name\": \"Instagram\", \"stats\": {\"lastDay\": \"488\"}}}}, \n" +
            "\"99bc-55fc-de3a-d175\": {\"name\": \"Seva\", \"friends\": [], \"stats\": {\"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"1927\"}}, \"com.minecraft.android\": {\"name\": \"Minecraft\", \"stats\": {\"lastDay\": \"737\"}}, \"com.tiktok.android\": {\"name\": \"TikTok\", \"stats\": {\"lastDay\": \"2832\"}}, \"com.crossy_road.android\": {\"name\": \"Crossy Road\", \"stats\": {\"lastDay\": \"1010\"}}, \"com.instagram.android\": {\"name\": \"Instagram\", \"stats\": {\"lastDay\": \"2677\"}}, \"com.twitter.android\": {\"name\": \"Twitter\", \"stats\": {\"lastDay\": \"2685\"}}}}, \n" +
            "\"d405-b8c1-bba0-2261\": {\"name\": \"Arseniy\", \"friends\": [], \"stats\": {\"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"62\"}}, \"com.minecraft.android\": {\"name\": \"Minecraft\", \"stats\": {\"lastDay\": \"223\"}}}}, \n" +
            "\"4141-34c4-83fa-1bdb\": {\"name\": \"Bob\", \"friends\": [], \"stats\": {\"com.crossy_road.android\": {\"name\": \"Crossy Road\", \"stats\": {\"lastDay\": \"1733\"}}, \"com.minecraft.android\": {\"name\": \"Minecraft\", \"stats\": {\"lastDay\": \"2709\"}}, \"com.instagram.android\": {\"name\": \"Instagram\", \"stats\": {\"lastDay\": \"1205\"}}, \"com.tiktok.android\": {\"name\": \"TikTok\", \"stats\": {\"lastDay\": \"2465\"}}, \"com.twitter.android\": {\"name\": \"Twitter\", \"stats\": {\"lastDay\": \"3536\"}}, \"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"1062\"}}}}, \n" +
            "\"3996-d773-4242-ab4a\": {\"name\": \"Anonymus\", \"friends\": [], \"stats\": {\"com.crossy_road.android\": {\"name\": \"Crossy Road\", \"stats\": {\"lastDay\": \"974\"}}, \"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"2692\"}}}}, \n" +
            "\"1718-c83b-c158-f9e8\": {\"name\": \"Gorge\", \"friends\": [], \"stats\": {\"com.twitter.android\": {\"name\": \"Twitter\", \"stats\": {\"lastDay\": \"851\"}}}}, \n" +
            "\"b6fa-7f16-be12-b6c9\": {\"name\": \"Lukas\", \"friends\": [], \"stats\": {\"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"2264\"}}}}, \n" +
            "\"7ad1-4cea-0c37-ef7d\": {\"name\": \"Ted\", \"friends\": [], \"stats\": {\"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"4\"}}}}, \n" +
            "\"23bd-a865-74f1-6a35\": {\"name\": \"trump\", \"friends\": [], \"stats\": {\"com.tiktok.android\": {\"name\": \"TikTok\", \"stats\": {\"lastDay\": \"21\"}}, \"com.minecraft.android\": {\"name\": \"Minecraft\", \"stats\": {\"lastDay\": \"242\"}}, \"com.instagram.android\": {\"name\": \"Instagram\", \"stats\": {\"lastDay\": \"237\"}}, \"com.twitter.android\": {\"name\": \"Twitter\", \"stats\": {\"lastDay\": \"53\"}}, \"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"272\"}}}}, \n" +
            "\"f4e1-f68f-ec70-aa4d\": {\"name\": \"Bob\", \"friends\": [], \"stats\": {\"com.you_tube.android\": {\"name\": \"You Tube\", \"stats\": {\"lastDay\": \"120\"}}, \"com.minecraft.android\": {\"name\": \"Minecraft\", \"stats\": {\"lastDay\": \"4\"}}, \"com.instagram.android\": {\"name\": \"Instagram\", \"stats\": {\"lastDay\": \"100\"}}, \"com.tiktok.android\": {\"name\": \"TikTok\", \"stats\": {\"lastDay\": \"132\"}}, \"com.crossy_road.android\": {\"name\": \"Crossy Road\", \"stats\": {\"lastDay\": \"15\"}}, \"com.twitter.android\": {\"name\": \"Twitter\", \"stats\": {\"lastDay\": \"84\"}}}}}}")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_display)

        var users = newJsonObj["content"] as JSONObject
        var activeUserId = "b025-d577-344e-1cb6"
        var daniel = users.getJSONObject(activeUserId)
        var arr = daniel["friends"] as JSONArray
        println(newJsonObj)
        addAnimals(arr,users)

        rv_animal_list.layoutManager = LinearLayoutManager(this)
        rv_animal_list.adapter = AnimalAdapter(animals, this,newJsonObj)

    }
    fun addAnimals(friends:JSONArray,users:JSONObject) {
        for (i in 0..(friends.length() - 1)) {
            val friend = friends.getString(i)
            val fobj = users.getJSONObject(friend)
            //animals.add(fobj.getString("name"))
            animals.add(friend)
        }
    }

}
