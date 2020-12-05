package com.reyad.psycheadminpanel.main.student

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.reyad.psycheadminpanel.R
import java.util.*


class StudentAdapter(
    val context: Context,
    private val items: List<StudentItem>,
    private val year: String,

    ) :
    RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_model, parent, false)
        return MyViewHolder(view)
    }

    //on binding
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mListPosition = items[position]
        holder.textViewName.text = mListPosition.name.split(' ')
            .joinToString(" ") { it.capitalize(Locale.ROOT) }

        holder.textViewId.text = mListPosition.id
        holder.textViewMobile.text = mListPosition.mobile

        // view image
        holder.imageViewProfile.load(mListPosition.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            transformations(CircleCropTransformation())
        }

        // edit image view
        holder.imageViewEdit.setOnClickListener {
            val intent = Intent(context, StudentUpdate::class.java)
            intent.putExtra("year", year)
            intent.putExtra("name", mListPosition.name)
            intent.putExtra("id", mListPosition.id)
            intent.putExtra("mobile", mListPosition.mobile)
            intent.putExtra("profileImg", mListPosition.imageUrl)
            context.startActivity(intent)
        }

        //delete image view
        holder.imageViewDelete.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Really want to delete this?")

            builder.setPositiveButton("Yes") { dialog, which ->
                // remove realtime database data
                FirebaseDatabase.getInstance().getReference("Student")
                    .child(year)
                    .child(mListPosition.id)
                    .removeValue()

                //remove fireStorage data
                FirebaseStorage.getInstance().getReference("Student")
                    .child(year)
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