package com.example.procrastinationgame

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.animal_list_item.view.*
import org.json.JSONObject

public class AnimalAdapter(val items : ArrayList<String>, val context: Context,val obj: JSONObject) : RecyclerView.Adapter<ViewHolder>() {
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    /*override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvAnimalType?.text = items.get(position)
    }*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))
    }
    // Binds each animal in the ArrayList to a view
    fun change_to_status(Id: String,name: String) {
        var intent = Intent(context, FriendStatActivity::class.java)
        intent.putExtra("USER_ID", Id);
        intent.putExtra("STRING_OBJ", obj.toString());
        startActivity(context, intent, null)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = items.get(position) as String
        val users = obj["content"] as JSONObject
        val user = users[id] as JSONObject
        val name = user["name"] as String

        //holder?.tvAnimalType?.text = items.get(position) //HERE
        holder?.tvAnimalType?.text = name
        holder?.tvAnimalType.setOnClickListener({
            change_to_status(id,name)
        })
        //var objiobj = obj["content"] as JSONObject
        //var item = items.get(position)
        //holder?.tvAnimalType?.text = objiobj[item] //HERE
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvAnimalType = view.tv_animal_type
}

