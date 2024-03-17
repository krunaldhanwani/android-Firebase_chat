package com.example.firebasedemochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(etEmail.text.toString()) && TextUtils.isEmpty(etPassword.text.toString())) {
                Toast.makeText(this,"email and password required",Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(etEmail.text.toString() , etPassword.text.toString())
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            etEmail.setText("")
                            etPassword.setText("")
                            startActivity(Intent(this,MainActivity::class.java))
                        } else {
                            Toast.makeText(this,"email and password is wrong",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }



    }
}