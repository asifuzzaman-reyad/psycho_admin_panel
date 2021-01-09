package com.reyad.psycheadminpanel.main.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reyad.psycheadminpanel.databinding.ActivityStudentViewBinding


class StudentView : AppCompatActivity() {

    lateinit var binding: ActivityStudentViewBinding

    var batch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get batch
        batch = intent.getStringExtra("batch")
        Log.i("studentView", "batch: $batch")
        binding.tvBatchStudentView.text = batch

        //
        loadStudentList()

        //
        binding.fabStudentView.setOnClickListener {
            val intent = Intent(this, StudentActivity::class.java).apply {
                putExtra("batch",batch.toString())
            }
            startActivity(intent)
        }
    }

    //
    private fun loadStudentList() {
        binding.progressBarStudentView.visibility = View.VISIBLE

        val db = FirebaseDatabase.getInstance().getReference("Students")
        val ref = db.child(batch.toString()).orderByChild("priority")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val mList = ArrayList<StudentItems>()

                if (snapshot.exists()) {
                    snapshot.children.forEach { dataSnapshot ->
                        val studentData = dataSnapshot.getValue(StudentItems::class.java)
                        Log.i("studentView", dataSnapshot.toString())

                        mList.add(studentData!!)
                        val mAdapter = StudentAdapter(this@StudentView, mList, batch.toString())
                        binding.recycleStudentDetails.setHasFixedSize(true)
                        binding.recycleStudentDetails.adapter = mAdapter

                        binding.progressBarStudentView.visibility = View.GONE

                    }
                } else {
                    binding.progressBarStudentView.visibility = View.GONE
                    binding.tvNoDataStudentView.visibility = View.VISIBLE

                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("studentView", error.message)
                binding.progressBarStudentView.visibility = View.INVISIBLE
            }

        })
    }

}
