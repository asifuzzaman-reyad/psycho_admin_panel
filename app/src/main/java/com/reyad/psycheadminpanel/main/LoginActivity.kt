package com.reyad.psycheadminpanel.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityLoginBinding
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        loadBatch()

        //button login
        binding.btnLoginLogin.setOnClickListener {
            val enteredBatch = binding.acBatchLogin.text.toString()
            val enteredPassword = binding.etPasswordLogin.text.toString()
            val enteredUser = binding.etUserLogin.text.toString()

            if (!validateBatch() || !validateUser() || !validatePassword()) {
                return@setOnClickListener

            } else {
//                verifyUserAndPassword(enteredBatch, enteredUser, enteredPassword)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java)
                    .apply { putExtra("batch", enteredBatch)})

            }

        }
    }

    private fun verifyUserAndPassword(batch: String, user: String, password: String) {
        binding.progressBarLogin.visibility = View.VISIBLE

        val db = FirebaseDatabase.getInstance().getReference("Admin").child(batch)
        val query = db.child(user)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dbPassword = snapshot.child("password").value.toString()
                Log.i("login", "user: ${snapshot.key} -> pass: $dbPassword")

                if (dbPassword == password) {
                    // motion toast
                    binding.progressBarLogin.visibility = View.GONE
                    motionToast()
                    //
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("batch", batch)
                    })
                } else {
                    binding.progressBarLogin.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "not match", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBarLogin.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "login error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

    //
    private fun motionToast() {
        MotionToast.createToast(
            this,
            "Hurray success 😍",
            "Login Completed successfully!",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

    //
    private fun loadBatch() {
        //list
        val batch = listOf("Batch 15", "Batch 14", "Batch 13", "Batch 12")

        //array adapter
        val adapterYear = ArrayAdapter(this, R.layout.material_spinner_item, batch)
        (binding.acBatchLogin as AutoCompleteTextView?)?.setAdapter(adapterYear)
    }


    //
    private fun validateBatch(): Boolean {
        val value = binding.acBatchLogin.text.toString()

        if (value.isEmpty()) {
            binding.tilBatchLogin.error = "Field can't be empty"
            return false
        } else {
            binding.tilBatchLogin.error = null
            binding.tilBatchLogin.isErrorEnabled = false
            return true
        }
    }

    //
    private fun validateUser(): Boolean {
        val value = binding.etUserLogin.text.toString()

        if (value.isEmpty()) {
            binding.tilUserLogin.error = "Field can't be empty"
            return false
        } else {
            binding.tilUserLogin.error = null
            binding.tilUserLogin.isErrorEnabled = false
            return true
        }
    }

    //
    private fun validatePassword(): Boolean {
        val value = binding.etPasswordLogin.text.toString()

        if (value.isEmpty()) {
            binding.tilPasswordLogin.error = "Field can't be empty"
            return false
        } else {
            binding.tilPasswordLogin.error = null
            binding.tilPasswordLogin.isErrorEnabled = false
            return true
        }
    }

}