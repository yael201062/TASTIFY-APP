//package com.example.tastify.ui.auth
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.tastify.R
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var etEmail: EditText
//    private lateinit var etPassword: EditText
//    private lateinit var btnLogin: Button
//    private lateinit var tvRegister: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        etEmail = findViewById(R.id.etEmail)
//        etPassword = findViewById(R.id.etPassword)
//        btnLogin = findViewById(R.id.btnLogin)
//        tvRegister = findViewById(R.id.tvRegister)
//
//        btnLogin.setOnClickListener {
//            val email = etEmail.text.toString().trim()
//            val password = etPassword.text.toString().trim()
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//            } else {
//                // כאן בעתיד נוסיף את חיבור ה-API
//                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, HomeActivity::class.java))
//                finish()
//            }
//        }
//
//        tvRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//            finish()
//        }
//    }
//}