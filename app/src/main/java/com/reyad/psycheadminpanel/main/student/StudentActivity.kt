package com.reyad.psycheadminpanel.main.student

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityStudentBinding
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*


const val REQUEST_SELECT_IMAGE = 111

class StudentActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var mUploadTask: StorageTask<*>? = null

    private lateinit var binding: ActivityStudentBinding
    var priority: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //oad session
        loadSessionAndId()

        //----------------------- upload button
        binding.btnUploadStudent.setOnClickListener {
            //
            uploadToFirebaseDatabase()
//            if (mUploadTask != null && mUploadTask!!.isInProgress) {
//                Toast.makeText(applicationContext, "Upload in progress", Toast.LENGTH_SHORT).show()
//            } else if (selectedImageUri == null) {
//                Toast.makeText(applicationContext, "No file selected", Toast.LENGTH_SHORT).show()
//                progressBar.visibility = View.INVISIBLE
//            } else {
////                uploadFileToFirebaseStorage(yearF!!, idF!!)
//            }
        }

    }

    // upload to realtime database
    private fun uploadToFirebaseDatabase() {
        val batch = intent.getStringExtra("batch").toString()
        val name = binding.etNameStudent.text.toString()
        val session = binding.acSessionStudent.text.toString()
        val id = binding.etIdStudent.text.toString()
        val mobile = binding.etMobileStudent.text.toString()

        //firebase instance
        val db = FirebaseDatabase.getInstance().getReference("Students")
        val ref = db.child(batch).child(id)
        //
        val studentMap: MutableMap<String, Any> = HashMap()
        studentMap["name"] = name
        studentMap["session"] = session
        studentMap["id"] = id
        studentMap["mobile"] = mobile
        studentMap["imageUrl"] = ""
        studentMap["priority"] = priority.toString()
        studentMap["hall"] = ""

        ref.updateChildren(studentMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Information upload successfully", Toast.LENGTH_SHORT)
                    .show()
                binding.progressBarStudent.visibility = View.INVISIBLE

            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "Database failed: ${it.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("study", "Database failed: ${it.message.toString()}")
                binding.progressBarStudent.visibility = View.INVISIBLE
            }
    }


    //load course code
    @SuppressLint("SetTextI18n")
    private fun loadSessionAndId() {
        //list
        val session15 = listOf("19-20", "18-19")
        val session14 = listOf("18-19", "17-18")
        val session13 = listOf("17-18", "16-17")
        val session12 = listOf("16-17", "15-16")

        val batch = intent.getStringExtra("batch").toString()
        //array adapter
        when (batch) {
            "Batch 15" -> {
                val adapterSession = ArrayAdapter(this, R.layout.material_spinner_item, session15)
                (binding.acSessionStudent as AutoCompleteTextView?)?.setAdapter(adapterSession)
            }
            "Batch 14" -> {
                val adapterSession = ArrayAdapter(this, R.layout.material_spinner_item, session14)
                (binding.acSessionStudent as AutoCompleteTextView?)?.setAdapter(adapterSession)
            }
            "Batch 13" -> {
                val adapterSession = ArrayAdapter(this, R.layout.material_spinner_item, session13)
                (binding.acSessionStudent as AutoCompleteTextView?)?.setAdapter(adapterSession)
            }
            "Batch 12" -> {
                val adapterSession = ArrayAdapter(this, R.layout.material_spinner_item, session12)
                (binding.acSessionStudent as AutoCompleteTextView?)?.setAdapter(adapterSession)
            }
        }


        //item click listener
        (binding.acSessionStudent as AutoCompleteTextView?)?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->

                when (position) {
                    0 -> {
                        binding.etIdStudent.setText("")
                        binding.tilIdStudent.visibility = View.VISIBLE
                        priority = "0"
                        Log.i("student", "priority: $priority")

                        val session = binding.acSessionStudent.text.toString()
                        val idModel = session.takeLast(2) + "6080"
                        binding.etIdStudent.setText(idModel)

                    }
                    1 -> {
                        binding.etIdStudent.setText("")
                        binding.tilIdStudent.visibility = View.VISIBLE
                        priority = "1"
                        val session = binding.acSessionStudent.text.toString()
                        val idModel = session.takeLast(2) + "6080"
                        binding.etIdStudent.setText(idModel)
                    }
                }
            }

    }

}

