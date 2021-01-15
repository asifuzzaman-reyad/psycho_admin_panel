package com.reyad.psycheadminpanel.main.study

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.reyad.psycheadminpanel.R
import com.squareup.picasso.Picasso

class MyItemAdapter(
    private val context: Context,
    private val itemList:List<ItemData>
): RecyclerView.Adapter<MyItemAdapter.MyViewHolder>() {

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener{
       var txt_title: TextView
       var img_item: ImageView

       lateinit var iItemClickListener: ItemClickListener

       fun setClick(iItemClickListener: ItemClickListener){
           this.iItemClickListener = iItemClickListener
       }

       init {
           txt_title = view.findViewById(R.id.tvTitle) as TextView
           img_item = view.findViewById(R.id.itemImage) as ImageView

           view.setOnClickListener(this)
       }
        override fun onClick(view: View?) {
            iItemClickListener.onItemClickListener(view!!, adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_title.setText(itemList[position].name)
        Picasso.get().load(itemList[position].image).into(holder.img_item)

        holder.setClick(object : ItemClickListener {
            override fun onItemClickListener(view: View, position: Int) {
                Toast.makeText(context, "clickOn:"+ itemList[position].name, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}