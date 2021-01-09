package com.reyad.psycheadminpanel.main.student

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.reyad.psycheadminpanel.R
import com.squareup.picasso.Picasso
import java.util.*


class StudentAdapter(
    val context: Context,
    private val items: List<StudentItems>,
    private val batch: String,

    ) :
    RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_model, parent, false)
        return MyViewHolder(view)
    }

    //on binding
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mListPosition = items[position]
        holder.textViewName.text = mListPosition.name.split(' ')
            .joinToString(" ") { it.capitalize(Locale.ROOT) }

        holder.textViewId.text = "Id: ${ mListPosition.id }"
        holder.textViewMobile.text = "Mobile: ${mListPosition.mobile}"

        if (mListPosition.imageUrl.isNotEmpty()) {
            Picasso.get().load(mListPosition.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageViewProfile)
        }

        // edit image view
        holder.imageViewEdit.setOnClickListener {
            val intent = Intent(context, StudentUpdate::class.java).apply {
                putExtra("batch", batch)
                putExtra("name", mListPosition.name)
                putExtra("id", mListPosition.id)
                putExtra("mobile", mListPosition.mobile)
                putExtra("profileImg", mListPosition.imageUrl)
            }
            context.startActivity(intent)
        }

        //delete image view
        holder.imageViewDelete.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Really want to delete this?")

            builder.setPositiveButton("Yes") { dialog, which ->
                // remove realtime database data
                FirebaseDatabase.getInstance().getReference("Student")
                    .child(batch)
                    .child(mListPosition.id)
                    .removeValue()

                //remove fireStorage data
                FirebaseStorage.getInstance().getReference("Student")
                    .child(batch)
                    .child(mListPosition.id)
                    .delete()

            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    //item count
    override fun getItemCount(): Int = items.size

    //view holder
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.tv_model_student_name)
        val textViewId: TextView = itemView.findViewById(R.id.tv_model_student_id)
        val textViewMobile: TextView = itemView.findViewById(R.id.tv_model_student_mobile)
        val imageViewProfile: ImageView = itemView.findViewById(R.id.iv_model_student_image)

        val imageViewEdit: ImageView = itemView.findViewById(R.id.iv_model_student_edit)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.iv_model_student_delete)
    }

}