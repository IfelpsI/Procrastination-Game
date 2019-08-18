package com.example.procrastinationgame

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_friend_display.*
import kotlinx.android.synthetic.main.animal_list_item.view.*
import kotlinx.android.synthetic.main.activity_friend_stat.view.*
import org.json.JSONObject

public class AnimalAdapter(val items: ArrayList<String>, val context: Context, val obj: JSONObject) :
    RecyclerView.Adapter<AnimalViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        return AnimalViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))
    }

    fun change_to_status(Id: String, name: String) {

        var intent = Intent(context, FriendStatActivity::class.java)
        val content = obj.getJSONObject("content")
        val person = content.getJSONObject(Id)
        val stats = person.getJSONObject("stats")
        val apps = stats.getJSONObject("apps")
        val name = person.getString("name")
        var statsKeys = ArrayList<String>()
        for (key in stats.keys()) {
            statsKeys.add(key)
        }
        intent.putExtra("USER_ID", Id)
        intent.putExtra("STATS", stats.toString())
        intent.putExtra("NAME", name)
        intent.putExtra("KEYS", statsKeys)
        startActivity(context, intent, null)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val id = items.get(position)
        val users = obj["content"] as JSONObject
        val user = users[id] as JSONObject
        val name = user["name"] as String

        holder?.tvAnimalType?.text = name
        holder?.tvAnimalType.setOnClickListener {
            change_to_status(id, name)
        }
    }
}

class AnimalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvAnimalType = view.tv_animal_type
}

