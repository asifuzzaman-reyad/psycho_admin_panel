package com.reyad.psycheadminpanel.main.teacher

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.main.teacher.ui.main.TeacherAbsentFragment
import com.reyad.psycheadminpanel.main.teacher.ui.main.TeacherInformation
import com.reyad.psycheadminpanel.main.teacher.ui.main.TeacherPagerAdapter
import com.reyad.psycheadminpanel.main.teacher.ui.main.TeacherPresentFragment

class TeacherDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_data)

        val teacherPagerAdapter = TeacherPagerAdapter(supportFragmentManager)
        teacherPagerAdapter.addFragment(TeacherPresentFragment(), "Present")
        teacherPagerAdapter.addFragment(TeacherAbsentFragment(),"Study Leave")


        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = teacherPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        val fab: FloatingActionButton = findViewById(R.id.fab_teacher)
        fab.setOnClickListener { view ->
            val intent = Intent(this, TeacherInformation::class.java)
            startActivity(intent)
        }
    }
}