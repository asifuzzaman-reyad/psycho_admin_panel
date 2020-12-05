package com.reyad.psycheadminpanel.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.reyad.psycheadminpanel.R

private const val PDF_REQUEST_CODE = 222

class StudyActivity : AppCompatActivity() {

    private lateinit var autoCom1: EditText
    private lateinit var autoCom2: EditText
    private lateinit var autoCom3: EditText

    private lateinit var autoComLayout3: TextInputLayout
    private lateinit var fileName: TextView
    private lateinit var progressBar: ProgressBar

    private var selectedFileUri: Uri? = null
    private var mUploadTask: StorageTask<*>? = null

    private lateinit var chooseFile: Button
    private lateinit var upload: Button

    var year: String? = null
    var admin: String? = null
    var password: String? = null

    var yearF: String? = null
    var codeF: String? = null
    var topicF: String? = null
    var topicF1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        // get share preference value
        val sharedPreferences =
            applicationContext?.getSharedPreferences("login", Context.MODE_PRIVATE)
        year = sharedPreferences?.getString("year", "")
        admin = sharedPreferences?.getString("admin", "")
        password = sharedPreferences?.getString("password", "")

        //hooks
        autoCom1 = findViewById(R.id.ac_study_course_code)
        autoCom2 = findViewById(R.id.ac_study_course_topic)
        autoCom3 = findViewById(R.id.ac_study_course_teacher)

        autoComLayout3 = findViewById(R.id.til_study_course_teacher)
        fileName = findViewById(R.id.tv_study_fileName)
        progressBar = findViewById(R.id.progressBar_study)

        chooseFile = findViewById(R.id.btn_study_choose_file)
        upload = findViewById(R.id.btn_study_upload)


        // ---------------------- autocomplete textView
        //course code
        loadCourseCode()

        // course topic
        loadCourseTopic()

        //----------------------- choose file button
        chooseFile.setOnClickListener {
            openFile()
        }

        //----------------------- upload button
        upload.setOnClickListener {
            yearF = year.toString()
            codeF = autoCom1.text.toString()
            topicF = autoCom2.text.toString()
            topicF1 = autoCom3.text.toString()

            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(applicationContext, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else if (selectedFileUri == null) {
                Toast.makeText(applicationContext, "No file selected", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                uploadFileToFirebaseStorage(yearF!!)
            }

        }
    }

    // upload file to storage
    private fun uploadFileToFirebaseStorage(year: String) {
//        if (!validCode() || !validShort() ||!validLesson()){
//            return
//        }
        progressBar.visibility = View.VISIBLE

        val fileName = System.currentTimeMillis().toString()

        val db = FirebaseStorage.getInstance().reference
        val ref = db.child("Study")
            .child(year)
            .child(fileName)

        mUploadTask = selectedFileUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener { it ->
                    Handler().postDelayed({
                        progressBar.progress = 0
                    }, 400)

                    Log.d("study", "File upload successfully")

                    ref.downloadUrl.addOnSuccessListener { uri ->
                        uploadToFirebase(uri.toString())
                        Log.d("study", "File url: $uri")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Storage failed: ${it.message.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("study", "Storage failed: ${it.message.toString()}")
                    progressBar.visibility = View.INVISIBLE
                }
                .addOnProgressListener { taskSnapshot ->
                    val currentProgress =
                        100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressBar.progress = currentProgress.toInt()
                }
        }
    }

    data class StudyItem(val code: String, val fileUrl: String) {
        constructor() : this("", "")
    }

    private fun uploadToFirebase(fileUrl: String) {

        val db = FirebaseDatabase.getInstance().reference
        val ref: DatabaseReference?
        ref = when {
            topicF.toString() == "Teacher1" -> {
                db.child("Study").child(year.toString()).child(topicF1.toString())
            }
            topicF.toString() == "Teacher2" -> {
                db.child("Study").child(year.toString()).child(topicF1.toString())
            }
            else -> {
                db.child("Study").child(year.toString()).child(topicF.toString())
            }
        }

        //
        val study = StudyItem(codeF!!, fileUrl)

        ref.push().setValue(study)
            .addOnSuccessListener {
                Toast.makeText(this, "File save successfully", Toast.LENGTH_SHORT)
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

    //open gallery
    private fun openFile() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
//            intent.type = "image/*"
//            intent.type = "audio/*"
//            intent.type = "video/*"
//            intent.type = "docx/*"
            intent.type = "application/pdf"
            startActivityForResult(intent, PDF_REQUEST_CODE)
        }
    }

    //view selected file
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
            }
            // show file name
            fileName.text = data?.data?.lastPathSegment
        } else {
            Toast.makeText(applicationContext, "Please select a file", Toast.LENGTH_SHORT).show()
        }
    }

    //load course code
    private fun loadCourseCode() {
        //list
        val code1 = listOf("psy 101", "psy 102", "psy 103", "psy 104", "psy 105", "psy 106")
        val code2 = listOf("psy 201", "psy 202", "psy 203", "psy 204", "psy 205", "psy 206")
        val code3 = listOf("psy 301", "psy 302", "psy 303", "psy 304", "psy 305", "psy 306")
        val code4 = listOf("psy 401", "psy 402", "psy 403", "psy 404", "psy 405", "psy 406")

        //array adapter
        val adapter1 = ArrayAdapter(this, R.layout.material_spinner_item, code1)
        val adapter2 = ArrayAdapter(this, R.layout.material_spinner_item, code2)
        val adapter3 = ArrayAdapter(this, R.layout.material_spinner_item, code3)
        val adapter4 = ArrayAdapter(this, R.layout.material_spinner_item, code4)

        // item click listener
        when (year) {
            "1st Year" -> (autoCom1 as AutoCompleteTextView?)?.setAdapter(adapter1)
            "2nd Year" -> (autoCom1 as AutoCompleteTextView?)?.setAdapter(adapter2)
            "3rd Year" -> (autoCom1 as AutoCompleteTextView?)?.setAdapter(adapter3)
            "4th Year" -> (autoCom1 as AutoCompleteTextView?)?.setAdapter(adapter4)
        }
    }

    // load course topic
    private fun loadCourseTopic() {
        //list
        val courseTopic = listOf("Questions", "Syllabus", "Notes", "Books", "Teacher1", "Teacher2")

        val courseTeacher1 = listOf("1st", "uk", "na", "ss", "al")
        val courseTeacher2 = listOf("2nd", "ma", "sz", "jn")
        val courseTeacher3 = listOf("3rd", "ma", "sz", "jn")
        val courseTeacher4 = listOf("4th", "ma", "sz", "jn")

        // array adapter
        val adapter = ArrayAdapter(this, R.layout.material_spinner_item, courseTopic)

        val adapter1 = ArrayAdapter(this, R.layout.material_spinner_item, courseTeacher1)
        val adapter2 = ArrayAdapter(this, R.layout.material_spinner_item, courseTeacher2)
        val adapter3 = ArrayAdapter(this, R.layout.material_spinner_item, courseTeacher3)
        val adapter4 = ArrayAdapter(this, R.layout.material_spinner_item, courseTeacher4)

        //item click listener
        (autoCom2 as AutoCompleteTextView?)?.setAdapter(adapter)
        (autoCom2 as AutoCompleteTextView?)?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->

                when (position) {
                    0 -> autoComLayout3.visibility = View.GONE
                    1 -> autoComLayout3.visibility = View.GONE
                    2 -> autoComLayout3.visibility = View.GONE
                    3 -> autoComLayout3.visibility = View.GONE
                    4 -> {
                        autoComLayout3.visibility = View.VISIBLE
                        when (year) {
                            "1st Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter1)
                            "2nd Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter2)
                            "3rd Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter3)
                            "4th Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter4)
                        }
                    }
                    5 -> {
                        autoComLayout3.visibility = View.VISIBLE
                        when (year) {
                            "1st Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter1)
                            "2nd Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter2)
                            "3rd Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter3)
                            "4th Year" -> (autoCom3 as AutoCompleteTextView?)?.setAdapter(adapter4)
                        }
                    }
                }
            }
    }
}