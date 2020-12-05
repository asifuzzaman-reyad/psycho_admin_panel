package com.reyad.psycheadminpanel.main.teacher.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.main.teacher.TeacherItemList

class TeacherInformation : AppCompatActivity() {

    private lateinit var autoComStatus: EditText
    private lateinit var autoComSerial: EditText
    private lateinit var autoComPost: EditText

    private lateinit var nameEditText: EditText
    private lateinit var phdEditText: EditText
    private lateinit var facebookEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var publicationEditText: EditText
    private lateinit var interestEditText: EditText
    private lateinit var mobileEditText: EditText

    private lateinit var uploadButton: Button

    var status: String? = null
    var serial: String? = null
    var post: String? = null

    var name: String? = null
    var phd: String? = null
    var facebook: String? = null
    var email: String? = null
    var mobile: String? = null
    var publication: String? = null
    var interest: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_information)

        //hooks
        // auto complete
        autoComStatus = findViewById(R.id.ac_teacher_status)
        autoComSerial = findViewById(R.id.ac_teacher_serial)
        autoComPost = findViewById(R.id.ac_teacher_post)

        // edit text
        nameEditText = findViewById(R.id.et_teacher_name)
        phdEditText = findViewById(R.id.et_teacher_phd)
        facebookEditText = findViewById(R.id.et_teacher_facebook)
        emailEditText = findViewById(R.id.et_teacher_email)
        publicationEditText = findViewById(R.id.et_teacher_publication)
        interestEditText = findViewById(R.id.et_teacher_interest)
        mobileEditText = findViewById(R.id.et_teacher_mobile)

        //button
        uploadButton = findViewById(R.id.btn_teacher_upload)

        // load function
        loadAutoCom()

        //
        uploadButton.setOnClickListener {

            status = autoComStatus.text.toString()
            serial = autoComSerial.text.toString()
            post = autoComPost.text.toString()

            name = nameEditText.text.toString()
            phd = phdEditText.text.toString()
            facebook = facebookEditText.text.toString()
            email = emailEditText.text.toString()
            mobile = mobileEditText.text.toString()
            publication = publicationEditText.text.toString()
            interest = interestEditText.text.toString()

            Log.i("test_teacher", name.toString())

            uploadToDatabase()
        }

    }

    private fun uploadToDatabase() {

        val db = FirebaseDatabase.getInstance().reference
        val ref = db.child("Teacher")
            .child(status.toString())
            .child(serial.toString())

        //

        val imageUrl = ""
        val teacherInfoList = TeacherItemList(
            name!!, post!!, phd!!, facebook!!, email!!,
            mobile!!, publication!!, interest!!, imageUrl
        )

        ref.setValue(teacherInfoList)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Teacher's Information Upload Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
//                progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Data uploading failed: ${it.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("study", "Data uploading failed: ${it.message.toString()}")
//                progressBar.visibility = View.INVISIBLE
            }
    }

    private fun loadAutoCom() {
        //list
        val status = listOf("Present", "Absent")
        val serial = listOf(
            "0","1", "2", "3", "4", "5", "6", "7", "8", "9",
        )
        val post = listOf(
            "Associate Professor", "Assistant Professor", "Lecturer",
        )

        //array adapter
        val adapterStatus = ArrayAdapter(this, R.layout.material_spinner_item, status)
        val adapterSerial = ArrayAdapter(this, R.layout.material_spinner_item, serial)
        val adapterPost = ArrayAdapter(this, R.layout.material_spinner_item, post)

        //item click listener
        (autoComStatus as AutoCompleteTextView?)?.setAdapter(adapterStatus)
        (autoComSerial as AutoCompleteTextView?)?.setAdapter(adapterSerial)
        (autoComPost as AutoCompleteTextView?)?.setAdapter(adapterPost)
    }

}