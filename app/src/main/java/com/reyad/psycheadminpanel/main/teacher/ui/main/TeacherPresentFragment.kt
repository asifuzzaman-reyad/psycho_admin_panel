package com.reyad.psycheadminpanel.main.teacher.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.main.teacher.TeacherItemList


class TeacherPresentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_teacher_present, container, false)

        val dbRef = FirebaseDatabase.getInstance().reference.child("Teacher").child("Present")

        val arrayList = ArrayList<TeacherItemList>()

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val data = it.getValue(TeacherItemList::class.java)
                    arrayList.add(data!!)
                    Log.i("teacher_view", "${data.name}  +  ${data.post}")

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return view
    }

}