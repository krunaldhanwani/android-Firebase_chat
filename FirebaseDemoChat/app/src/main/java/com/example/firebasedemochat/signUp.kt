package com.example.firebasedemochat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class signUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private val PICK_IMAGE_REQUEST: Int = 2020
    private var imageLink: Uri? = null

    private var image = ""
    var storageReference: StorageReference? = null
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        btnSubmit.setOnClickListener {
            val userName = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(applicationContext, "userName is Required", Toast.LENGTH_SHORT)
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "email is Required", Toast.LENGTH_SHORT)
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "password is Required", Toast.LENGTH_SHORT)
            } else if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(applicationContext, "confirmPassword is Required", Toast.LENGTH_SHORT)
            } else if (password != confirmPassword) {
                Toast.makeText(applicationContext, "Password Must Be Same", Toast.LENGTH_SHORT)
            } else {
                registerUser(userName,  email, password,image.toUri())
            }
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        ivProfile.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Image"), PICK_IMAGE_REQUEST)
        }
    }

    private fun registerUser(userName: String, email: String, password: String, profileImage: Uri?) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    var user: FirebaseUser? = auth.currentUser
                    var userId: String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userId", userId)
                    hashMap.put("userName", userName)
                    hashMap.put("profileImage", profileImage.toString())

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageLink = data!!.data
                var auth = FirebaseAuth.getInstance()
                var user: FirebaseUser? = auth.currentUser

                Glide.with(this)
                    .load(imageLink)
                    .into(ivProfile)

                storageReference = storageReference!!.child(UUID.randomUUID().toString())

                storageReference!!.putFile(imageLink!!)
                    .addOnSuccessListener(
                        OnSuccessListener<UploadTask.TaskSnapshot?> {
                            Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                        })
                    .addOnFailureListener(OnFailureListener { e -> // Err
                        // or, Image not uploaded
                        Toast.makeText(this, "Failure$e", Toast.LENGTH_SHORT).show()
                    })
                    .addOnProgressListener(
                        OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        })

                addImageMessage(imageLink.toString())

            }
        }
    }

    private fun addImageMessage(url : String) {
        val metadata = StorageMetadata.Builder()
        metadata.setCustomMetadata("contentType", "image/jpeg")
        val strURL = FirebaseStorage.getInstance().getReference("Images")
            .child(url+ "TestImage")
            .putFile(Uri.parse(url), metadata.build())
        strURL.addOnSuccessListener {
            FirebaseStorage.getInstance().getReference("Images")
                .child(url + "TestImage").downloadUrl.addOnSuccessListener {
                    image = it.toString()
                    Log.e("image","11"+image)
                }.addOnFailureListener {
                    Log.e("Firebase", "Failed in downloading")
                }
        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail $it")
        }

    }
}