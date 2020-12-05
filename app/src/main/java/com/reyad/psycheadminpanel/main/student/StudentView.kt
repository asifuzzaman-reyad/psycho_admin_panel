 package com.reyad.psycheadminpanel.main.student

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reyad.psycheadminpanel.R


class StudentView : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    var year: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_view)

        // hooks
        recyclerView = findViewById(R.id.recycle_student_details)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // get share preference value
        val sharedPreferences =
            applicationContext?.getSharedPreferences("login", Context.MODE_PRIVATE)
        year = sharedPreferences?.getString("year", "")

        val db = FirebaseDatabase.getInstance().reference
        val ref = db.child("Student").child(year.toString())
//        val ref = db.child("Student").child("Batch 14")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val mList = ArrayList<StudentItem>()

                snapshot.children.forEach { dataSnapshot ->
                    val studentData = dataSnapshot.getValue(StudentItem::class.java)
                    Log.i("student", dataSnapshot.toString())
                    
                    mList.add(studentData!!)
                    val mAdapter = StudentAdapter(this@StudentView, mList, year.toString() )
                    recyclerView.adapter =mAdapter

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val fab = findViewById<FloatingActionButton>(R.id.fab_student)
        fab.setOnClickListener {
            val intent = Intent(this, StudentActivity::class.java)
            startActivity(intent)
        }

    }
}