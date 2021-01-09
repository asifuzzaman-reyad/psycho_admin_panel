package com.reyad.psycheadminpanel.main.student

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityStudentUpdateBinding
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*

class StudentUpdate : AppCompatActivity() {

    lateinit var binding: ActivityStudentUpdateBinding
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val batch = intent.getStringExtra("batch").toString()
        val name = intent.getStringExtra("name").toString()
        val id = intent.getStringExtra("id").toString()
        val mobile = intent.getStringExtra("mobile").toString()
        val profileImg = intent.getStringExtra("profileImg").toString()
        Log.i("studentUpdate", "$batch ->> $id ->> $name")


        // edit
        binding.etContentName.setText(name)
        binding.etContentMobile.setText(mobile)
        if (profileImg.isNotEmpty()) {
            Picasso.get().load(profileImg).placeholder(R.drawable.placeholder)
                .into(binding.ivProfileStudentUpdate)
        }

        //
        binding.buttonContentUpdate.setOnClickListener {
            binding.progressBarStudentUpdate.visibility = View.VISIBLE

            val map: MutableMap<String, Any> = HashMap()
            map["name"] = binding.etContentName.text.toString()
            map["mobile"] = binding.etContentMobile.text.toString()

            FirebaseDatabase.getInstance().reference
                .child("Students")
                .child(batch).child(id)
                .updateChildren(map)
                .addOnCompleteListener {
                    Log.i("studentUpdate", "update name, mobile")

                    binding.progressBarStudentUpdate.visibility = View.INVISIBLE

                    Toast.makeText(this, "Profile update successfully 😍", Toast.LENGTH_LONG).show()

//                    val intent = Intent(this@StudentUpdate, StudentView::class.java).apply {
//                        putExtra("batch", batch)
//                    }
//                    startActivity(intent)
//                    finish()
                }.addOnFailureListener {
                    binding.progressBarStudentUpdate.visibility = View.INVISIBLE
                    Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_SHORT).show()
                }

        }

        //
        binding.ivProfileStudentUpdate.setOnClickListener {
            openGallery()
        }
    }

    //
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
                Log.i("studentUpdate", "image uri: $selectedImageUri")

                CropImage.activity(selectedImageUri)
                    .setAspectRatio(1, 1)
                    .start(this)


            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                selectedImageUri = result.uri

                progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading Image...")
                progressDialog.setMessage("Please wait some time")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                //
                imageUploadToStorage()

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    //
    private fun imageUploadToStorage() {
        val batch = intent.getStringExtra("batch").toString()
        val id = intent.getStringExtra("id").toString()

        val db = FirebaseStorage.getInstance().reference
        val ref = db.child("Students")
            .child(batch).child("$id.jpg")

        selectedImageUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener {
                    Log.d("studentUpdate", "image upload successfully")

                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        //
                        uploadToFirebase(downloadUri.toString(), batch, id)
                        Log.d("studentUpdate", "Image url: $downloadUri")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Storage failed: ${it.message.toString()}",
                        Toast.LENGTH_SHORT).show()
                    Log.i("studentUpdate", "Storage failed: ${it.message.toString()}")
                    progressDialog.dismiss()
                }
        }
    }

    //
    private fun uploadToFirebase(downloadUri: String, batch: String, id: String) {

        val map: MutableMap<String, Any> = HashMap()
        map["imageUrl"] = downloadUri

        FirebaseDatabase.getInstance().reference
            .child("Students")
            .child(batch).child(id)
            .updateChildren(map)
            .addOnCompleteListener {
                Log.i("studentUpdate", "update imageUrl")

                progressDialog.dismiss()

                Toast.makeText(this, "Profile Image update successfully 😍", Toast.LENGTH_LONG).show()

                val intent = Intent(this@StudentUpdate, StudentView::class.java).apply {
                    putExtra("batch", batch)
                }
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}



