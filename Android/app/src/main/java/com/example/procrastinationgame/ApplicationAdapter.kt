package com.example.procrastinationgame

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.animal_list_item.view.*
import org.json.JSONObject

public class ApplicationAdapter(val items: ArrayList<String>, val context: Context, val obj: JSONObject) :
    RecyclerView.Adapter<AnimalViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        return AnimalViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val id = items.get(position)
        val users = obj["content"] as JSONObject
        val user = users[id] as JSONObject
        val name = user["name"] as String

        holder?.tvAnimalType?.text = name
    }
}

class ApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimalType = view.tv_animal_type
}

