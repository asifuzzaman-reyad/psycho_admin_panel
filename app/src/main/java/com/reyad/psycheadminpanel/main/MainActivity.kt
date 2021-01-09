package com.reyad.psycheadminpanel.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityMainBinding
import com.reyad.psycheadminpanel.main.student.StudentView
import com.reyad.psycheadminpanel.main.teacher.TeacherDataActivity

class MainActivity : AppCompatActivity() {

    private lateinit var studyBtn: MaterialButton
    private lateinit var studentBtn: MaterialButton
    private lateinit var teacherBtn: MaterialButton

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studyBtn = findViewById(R.id.btn_study_home)
        studentBtn = findViewById(R.id.btn_student_home)
        teacherBtn = findViewById(R.id.btn_teacher_home)

        // get batch
        val getBatch = intent.getStringExtra("batch")
        Log.i("main", "getBatch: ->> $getBatch")

        studyBtn.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java).apply {
                putExtra("batch", getBatch.toString())
            }
            startActivity(intent)
        }

        studentBtn.setOnClickListener {
            val intent = Intent(this, StudentView::class.java).apply {
                putExtra("batch", getBatch.toString())
            }
            startActivity(intent)
        }

        teacherBtn.setOnClickListener {
            val intent = Intent(this, TeacherDataActivity::class.java)
            startActivity(intent)
        }


    }
}