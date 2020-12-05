package com.reyad.psycheadminpanel.main.student

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityStudentUpdateBinding
import java.util.*

class StudentUpdate : AppCompatActivity() {

    lateinit var binding: ActivityStudentUpdateBinding
    private var selectedImageUri: Uri? = null

    private lateinit var year: String
    private lateinit var name: String
    private lateinit var id: String
    private lateinit var mobile: String
    private lateinit var profileImg: String

    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        year = intent.getStringExtra("year").toString()
        name = intent.getStringExtra("name").toString()
        id = intent.getStringExtra("id").toString()
        mobile = intent.getStringExtra("mobile").toString()
        profileImg = intent.getStringExtra("profileImg").toString()

        progressBar = binding.progressBar

        // edit
        binding.apply {
            //
            etContentName.setText(name)
            etContentMobile.setText(mobile)

            //view image
            ivContentProfile.load(profileImg) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                transformations(CircleCropTransformation())
            }

            //
            binding.ivContentProfile.setOnClickListener {
                openGallery()
            }

            //
            binding.buttonContentUpdate.setOnClickListener {
                val map: MutableMap<String, Any> = HashMap()
                map["name"] = binding.etContentName.text.toString()
                map["mobile"] = binding.etContentMobile.text.toString()

                FirebaseDatabase.getInstance().reference
                    .child("Student")
                    .child(year)
                    .child(id)
                    .updateChildren(map)
                    .addOnCompleteListener {
                        Log.i("studentDetails", "update")

                        val intent =
                            Intent(this@StudentUpdate, StudentView::class.java)
                        startActivity(intent)
                    }

                if (selectedImageUri != null) {
                    uploadFileToFirebaseStorage(year, id)
                }
            }
        }

    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
//            startActivityForResult(intent, PDF_REQUEST_CODE)
            startActivityForResult(
                Intent.createChooser(intent, "Choose Picture"),
                REQUEST_SELECT_IMAGE
            )
        }
    }

    //view selected image
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                Log.i("studentDetails", "image uri: $selectedImageUri")
            }
            // show file name
            binding.ivContentProfile.load(selectedImageUri) {
                crossfade(true)
                placeholder(R.drawable.no_image_selected)
                transformations(CircleCropTransformation())
            }

        } else {
            Toast.makeText(applicationContext, "Please select an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadFileToFirebaseStorage(year: String, id: String) {
//        if (!validCode() || !validShort() ||!validLesson()){
//            return
//        }
        progressBar.visibility = View.VISIBLE

        val db = FirebaseStorage.getInstance().reference
        val ref = db.child("Student")
            .child(year)
            .child(id)

//        mUploadTask = selectedImageUri?.let { it ->
        selectedImageUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener { it ->

                    Handler().postDelayed({
                        progressBar.progress = 0
                    }, 500)

                    Log.d("student", "image upload successfully")

                    ref.downloadUrl.addOnSuccessListener { uri ->

                        val map: MutableMap<String, Any> = HashMap()
                        map["imageUrl"] = uri.toString()

                        FirebaseDatabase.getInstance().reference
                            .child("Student")
                            .child(year)
                            .child(id)
                            .updateChildren(map)
                            .addOnCompleteListener {
                                Log.i("studentDetails", "update img")
                                progressBar.visibility = View.INVISIBLE
                            }

                        Log.d("student", "Image url: $uri")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Storage failed: ${it.message.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("student", "Storage failed: ${it.message.toString()}")
                    progressBar.visibility = View.INVISIBLE
                }
                .addOnProgressListener { taskSnapshot ->
                    val currentProgress =
                        100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressBar.progress = currentProgress.toInt()
                }
        }
    }


}