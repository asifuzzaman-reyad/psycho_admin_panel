package com.reyad.psycheadminpanel.main.student

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.reyad.psycheadminpanel.R

const val REQUEST_SELECT_IMAGE = 111
class StudentActivity : AppCompatActivity() {

    private lateinit var autoComSession: EditText
    private lateinit var autoComIdLayout: TextInputLayout

    private lateinit var nameEt: EditText
    private lateinit var mobileEt: EditText
    private lateinit var idEt: EditText

    private lateinit var profileIv: ImageView

    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null
    private var mUploadTask: StorageTask<*>? = null

    private lateinit var chooseImage: Button
    private lateinit var upload: Button

    var year: String? = null
    var yearF: String? = null
    var sessionF: String? = null
    var idF: String? = null
    var nameF: String? = null
    var mobileF: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        // get share preference value
        val sharedPreferences =
            applicationContext?.getSharedPreferences("login", Context.MODE_PRIVATE)
        year = sharedPreferences?.getString("year", "")

        //hooks
        autoComSession = findViewById(R.id.ac_student_session)
        idEt = findViewById(R.id.et_student_id)
        progressBar = findViewById(R.id.progressBar_student)

        autoComIdLayout = findViewById(R.id.til_student_id)

        nameEt = findViewById(R.id.et_student_name)
        idEt = findViewById(R.id.et_student_id)
        mobileEt = findViewById(R.id.et_student_mobile)
        profileIv = findViewById(R.id.et_student_profile_img)

        chooseImage = findViewById(R.id.btn_student_choose_image)
        upload = findViewById(R.id.btn_student_upload)

        //oad session
        loadSession()

        //----------------------- choose image button
        chooseImage.setOnClickListener {
            openGallery()
        }

        //----------------------- upload button
        upload.setOnClickListener {
            yearF = year.toString()
            sessionF = autoComSession.text.toString()
            nameF = nameEt.text.toString()
            idF = idEt.text.toString()
            mobileF = mobileEt.text.toString()

            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(applicationContext, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else if (selectedImageUri == null) {
                Toast.makeText(applicationContext, "No file selected", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            } else {
                uploadFileToFirebaseStorage(yearF!!, idF!!)
            }
        }

    }

    // upload image to storage
    private fun uploadFileToFirebaseStorage(year: String, id: String) {
//        if (!validCode() || !validShort() ||!validLesson()){
//            return
//        }
            progressBar.visibility = View.VISIBLE

//        val fileName = System.currentTimeMillis().toString() + ".jpg"

        val db = FirebaseStorage.getInstance().reference
        val ref = db.child("Student")
            .child(year)
            .child(id)

        mUploadTask = selectedImageUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener { it ->

                    Handler().postDelayed({
                    progressBar.progress = 0
                    }, 300)

                    Log.d("student", "image upload successfully")

                    ref.downloadUrl.addOnSuccessListener { uri ->
                        uploadToFirebase(uri.toString())
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

    // upload to realtime database
    private fun uploadToFirebase(imageUrl: String) {
        //firebase instance
        val db = FirebaseDatabase.getInstance().reference
        val ref = db.child("Student")
            .child(year.toString())
            .child(idF!!)

        //
        val student = StudentItem(
            nameF!!, sessionF!!, idF!!, mobileF!!, imageUrl
        )

        ref.setValue(student)
            .addOnSuccessListener {
                Toast.makeText(this, "Image save successfully", Toast.LENGTH_SHORT)
                    .show()
                progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Database failed: ${it.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("study", "Database failed: ${it.message.toString()}")
                progressBar.visibility = View.INVISIBLE
            }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
//            startActivityForResult(intent, PDF_REQUEST_CODE)
            startActivityForResult(Intent.createChooser(intent, "Choose Picture"), REQUEST_SELECT_IMAGE)
        }
    }

    //view selected image
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                Log.i("student", "image uri: $selectedImageUri")
            }
            // show file name
            profileIv.load(selectedImageUri) {
                crossfade(true)
                placeholder(R.drawable.no_image_selected)
                transformations(CircleCropTransformation())
            }

        } else {
            Toast.makeText(applicationContext, "Please select an Image", Toast.LENGTH_SHORT).show()
        }
    }

    //load course code
    @SuppressLint("SetTextI18n")
    private fun loadSession() {
        //list
        val session = listOf("19-20", "18-19", "17-18", "16-17")

        //array adapter
        val adapterSession = ArrayAdapter(this, R.layout.material_spinner_item, session)

        //item click listener
        (autoComSession as AutoCompleteTextView?)?.setAdapter(adapterSession)
        (autoComSession as AutoCompleteTextView?)?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->

                when (position) {
                    0 -> {
                        idEt.setText("")
                        autoComIdLayout.visibility = View.VISIBLE
                        idEt.setText("206080")

                    }
                    1 -> {
                        idEt.setText("")
                        autoComIdLayout.visibility = View.VISIBLE
                        idEt.setText("196080")
                    }
                    2 -> {
                        idEt.setText("")
                        autoComIdLayout.visibility = View.VISIBLE
                        idEt.setText("186080")
                    }
                    3 -> {
                        idEt.setText("")
                        autoComIdLayout.visibility = View.VISIBLE
                        idEt.setText("176080")
                    }
                }
            }

    }
}

