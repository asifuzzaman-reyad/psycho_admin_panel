package com.reyad.psycheadminpanel.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.reyad.psycheadminpanel.R
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity() {

    private lateinit var autoComYear: EditText
    private lateinit var adminEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        //hooks
        autoComYear = findViewById(R.id.ac_login_year)
        adminEt = findViewById(R.id.et_login_admin)
        passwordEt = findViewById(R.id.et_login_password)
        loginBtn = findViewById(R.id.btn_login)

        //list
        val batch = listOf("Batch 15", "Batch 14", "Batch 13", "Batch 12")

        //array adapter
        val adapterYear = ArrayAdapter(this, R.layout.material_spinner_item, batch)
        (autoComYear as AutoCompleteTextView?)?.setAdapter(adapterYear)

        //button login
        loginBtn.setOnClickListener {
            val year = autoComYear.text.toString()
            val admin = adminEt.text.toString()
            val password = passwordEt.text.toString()

            val intent = Intent(this, MainActivity::class.java)
            studySharedPref(year, admin, password)
            startActivity(intent)

            //
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
    }

    private fun studySharedPref(
        value1: String, value2: String, value3: String
    ) {
        val sharedPreferences =
            applicationContext?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("year", value1)
        editor?.putString("admin", value2)
        editor?.putString("password", value3)
        editor?.apply()
    }
}