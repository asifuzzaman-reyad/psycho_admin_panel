package com.reyad.psycheadminpanel.main

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityStudyBinding

private const val PDF_REQUEST_CODE = 222

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding

    private lateinit var progressBar: ProgressBar

    private var selectedFileUri: Uri? = null
    private var mUploadTask: StorageTask<*>? = null


    var batch: String? = null
    var year: String? = null
    var courseCode: String? = null
    var topic: String? = null
    var chapter: String? = null
    var lesson: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hooks
        progressBar = findViewById(R.id.progressBar_study)

        // ---------------------- autocomplete textView
        batch = intent.getStringExtra("batch").toString()
        when (batch) {
            "Batch 15" -> {
                year = "1st Year"
            }
            "Batch 14" -> {
                year = "2nd Year"
            }
            "Batch 13" -> {
                year = "3rd Year"
            }
            "Batch 12" -> {
                year = "4th Year"
            }
        }

        //course code
        loadCourseCode()
        loadCourseTopic()
        loadChapterNo()

        //--- choose file button
        binding.btnChooseFileStudy.setOnClickListener {
            openFile()
        }

        //--- upload button
        binding.btnUploadStudy.setOnClickListener {

            courseCode = binding.acCourseCodeStudy.text.toString()
            topic = binding.acCourseTopicStudy.text.toString()
            chapter = binding.acChapterNoStudy.text.toString()
            lesson = binding.acLessonNoStudy.text.toString()

            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(applicationContext, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else if (selectedFileUri == null) {
                Toast.makeText(applicationContext, "No file selected", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                uploadFileToFirebaseStorage()
            }

        }
    }


    // upload file to storage
    private fun uploadFileToFirebaseStorage() {
//        if (!validCode() || !validShort() ||!validLesson()){
//            return
//        }
        progressBar.visibility = View.VISIBLE

        val fileName = System.currentTimeMillis().toString()

        val db = FirebaseStorage.getInstance().reference
        val ref = db.child("Study")
            .child(year!!)
            .child(fileName + "pdf")

        mUploadTask = selectedFileUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener {
                    Handler().postDelayed({
                        progressBar.progress = 0
                    }, 400)
                    Log.d("study", "File upload successfully")

                    ref.downloadUrl.addOnSuccessListener { uri ->
                        //
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


    // firebase database
    private fun uploadToFirebase(fileUrl: String) {
        val db = FirebaseDatabase.getInstance().reference
        val ref = db.child("Study").child(year.toString()).child(topic.toString())

        //
        val studyMap: MutableMap<String, Any> = HashMap()
        studyMap["courseCode"] = courseCode.toString()
        studyMap["chapter"] = chapter.toString()
        studyMap["lesson"] = lesson.toString()
        studyMap["fileUrl"] = fileUrl


        ref.push().setValue(studyMap)
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
            "1st Year" -> (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter1)
            "2nd Year" -> (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter2)
            "3rd Year" -> (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter3)
            "4th Year" -> (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter4)
        }
    }

    // load course topic
    private fun loadCourseTopic() {
        //list
        val courseTopic = listOf("Notes", "Teacher", "Books", "Questions", "Syllabus")

        val courseTeacher1 = listOf("1st", "uk", "na", "ss", "al")
        val courseTeacher2 = listOf("2nd", "ma", "sz", "jn")
        val courseTeacher3 = listOf("3rd", "ma", "sz", "jn")
        val courseTeacher4 = listOf("4th", "ma", "sz", "jn")

        // array adapter
        val adapter = ArrayAdapter(this, R.layout.material_spinner_item, courseTopic)


        //item click listener
        (binding.acCourseTopicStudy as AutoCompleteTextView?)?.setAdapter(adapter)
        (binding.acCourseTopicStudy as AutoCompleteTextView?)?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->

                when (position) {

                    0 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.VISIBLE
                    }

                    1 -> {
                        binding.linearNotesStudy.visibility = View.GONE
                        binding.tilCourseTeacherStudy.visibility = View.VISIBLE
                        binding.linearNotesStudy.visibility = View.VISIBLE

                        val teacherAc = binding.acCourseTeacherStudy as AutoCompleteTextView?
                        when (year) {
                            "1st Year" -> {
                                teacherAc?.setAdapter(
                                    ArrayAdapter(
                                        this,
                                        R.layout.material_spinner_item,
                                        courseTeacher1
                                    )
                                )
                            }
                            "2nd Year" -> {
                                teacherAc?.setAdapter(
                                    ArrayAdapter(
                                        this,
                                        R.layout.material_spinner_item,
                                        courseTeacher2
                                    )
                                )
                            }
                            "3rd Year" -> {
                                teacherAc?.setAdapter(
                                    ArrayAdapter(
                                        this,
                                        R.layout.material_spinner_item,
                                        courseTeacher3
                                    )
                                )
                            }
                            "4th Year" -> {
                                teacherAc?.setAdapter(
                                    ArrayAdapter(
                                        this,
                                        R.layout.material_spinner_item,
                                        courseTeacher4
                                    )
                                )
                            }
                        }
                    }

                    2 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.GONE
                    }

                    3 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.GONE
                    }

                    4 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.GONE
                    }

                }
            }
    }

    // load Chapter No
    private fun loadChapterNo() {
        
        val chapter2 = listOf(
            "1.Field",
            "2.Research",
            "3.Theories",
            "4.Prenatal",
            "5.The Birth",
            "6.The Neonate",
            "7.Infancy",
            "8.Adolescence",
        )

        //array adapter
        val chapterAdapter2 = ArrayAdapter(this, R.layout.material_spinner_item, chapter2)

        (binding.acChapterNoStudy as AutoCompleteTextView?)?.setAdapter(chapterAdapter2)

        val lessonNo = binding.acChapterNoStudy.text.toString()
        Log.i("study", lessonNo)

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
            binding.tvFileNameStudy.text = "One File selected"
        } else {
            Toast.makeText(applicationContext, "Please select a file", Toast.LENGTH_SHORT).show()
        }
    }

}