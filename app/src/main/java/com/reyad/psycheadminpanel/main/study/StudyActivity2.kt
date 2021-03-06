package com.reyad.psycheadminpanel.main.study

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.reyad.psycheadminpanel.R
import dmax.dialog.SpotsDialog

class StudyActivity2 : AppCompatActivity() {

    lateinit var dialog: AlertDialog
    lateinit var myData: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study2)

        dialog = SpotsDialog.Builder().setContext(this).build()
        myData = FirebaseDatabase.getInstance().getReference("Course").child("MyData")

        getFirebaseData()
    }

    private fun getFirebaseData() {
        dialog.show()
        myData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val itemGroupA = ArrayList<ItemGroup>()
                    for (myDataSnapShot in snapshot.children) {
                        val itemGroup = ItemGroup()
                        itemGroup.headerTitle =
                            myDataSnapShot.child("headerTitle").getValue(true).toString()
                        Log.i("study2", myDataSnapShot.child("headerTitle").value.toString())
                        val t = object : GenericTypeIndicator<ArrayList<ItemData>>() {}
                        itemGroup.itemList = myDataSnapShot.child("listItem").getValue(t)
                        itemGroupA.add(itemGroup)

                        val adapter = MyGroupAdapter(this@StudyActivity2, itemGroupA)
                        findViewById<RecyclerView>(R.id.recycler_view_study2).adapter = adapter
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(this@StudyActivity2, "not yet uploaded", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("study2", error.message)
                dialog.dismiss()
            }
        })
    }

}