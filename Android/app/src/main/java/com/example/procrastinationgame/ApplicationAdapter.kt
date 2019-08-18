package com.example.procrastinationgame

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_list_item.view.*
import org.json.JSONObject

public class ApplicationAdapter(val items: ArrayList<String>, val context: Context, val obj: JSONObject) :
    RecyclerView.Adapter<ApplicationViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        return ApplicationViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val key = items.get(position)

        if (key != "unlock_screen") {
            val progStats = obj.get(key) as JSONObject
            val progName = progStats.getString("name").capitalize()
            val progTime = progStats.getString("time")
            holder?.AppName?.text = progName
            holder?.TimeSpent?.text = progTime
        } else {
            holder?.AppName?.text = "Unlocked Screen"
            holder?.TimeSpent?.text = obj.getString(key)
        }

    }
}

class ApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val AppName = view.appname
    val TimeSpent = view.timespent
}

