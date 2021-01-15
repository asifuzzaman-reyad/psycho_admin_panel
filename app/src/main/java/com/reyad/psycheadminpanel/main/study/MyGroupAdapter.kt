package com.reyad.psycheadminpanel.main.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reyad.psycheadminpanel.R

class MyGroupAdapter(
    private val context: StudyActivity2,
    private val dataList:List<ItemGroup>
) :RecyclerView.Adapter<MyGroupAdapter.MyViewHolder>(){

    //
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var itemTitle: TextView
        var recyclerView: RecyclerView

        init {
            itemTitle = view.findViewById(R.id.itemTitle) as TextView
            recyclerView = view.findViewById(R.id.recycler_view_list) as RecyclerView
        }
    }

    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_group, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemTitle.text = dataList[position].headerTitle

        val items = dataList[position].itemList
        val itemListAdapter = MyItemAdapter(context, items!!)
        holder.recyclerView.setHasFixedSize(true)
        holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerView.adapter = itemListAdapter
        holder.recyclerView.isNestedScrollingEnabled = false  // important

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}