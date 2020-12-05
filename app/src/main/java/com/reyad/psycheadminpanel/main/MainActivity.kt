package com.reyad.psycheadminpanel.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.main.student.StudentActivity
import com.reyad.psycheadminpanel.main.student.StudentView
import com.reyad.psycheadminpanel.main.teacher.TeacherDataActivity

class MainActivity : AppCompatActivity() {

    private lateinit var studyBtn: MaterialButton
    private lateinit var studentBtn: MaterialButton
    private lateinit var teacherBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studyBtn = findViewById(R.id.btn_home_study)
        studentBtn = findViewById(R.id.btn_home_student)
        teacherBtn = findViewById(R.id.btn_home_teacher)

        studyBtn.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }

        studentBtn.setOnClickListener {
            val intent = Intent(this, StudentView::class.java)
            startActivity(intent)
        }

        teacherBtn.setOnClickListener {
                val intent = Intent(this, TeacherDataActivity::class.java)
                startActivity(intent)
        }


    }
}