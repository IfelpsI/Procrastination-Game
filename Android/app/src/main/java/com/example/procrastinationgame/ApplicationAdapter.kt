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
        return ApplicationViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val key = items.get(position)
        val progStats = obj.get(key)

        holder?.rvAnimalType?.text = progStats.toString()
    }
}

class ApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvAnimalType = view.rv_animal_type2
}

