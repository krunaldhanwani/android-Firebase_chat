package com.example.firebasedemochat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    var userList = ArrayList<user>()
    var chatList = ArrayList<chat>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userList()
        userRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val userAdapter = userAdapter(userList)
        userRecyclerView.adapter = userAdapter
    }

    private fun userList() {
        val firebase:FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val databaseReference2:DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference2.addValueEventListener(object :ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (dataSnapShot:DataSnapshot in snapshot.children) {

                    val chat = dataSnapShot.getValue(chat::class.java)

                        chatList.add(chat!!)
                    }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"hello",Toast.LENGTH_SHORT).show()
            }

        })

        databaseReference.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (dataSnapShot:DataSnapshot in snapshot.children) {

                    val user = dataSnapShot.getValue(user::class.java)

                    if (user!!.userId != firebase.uid) {
                        userList.add(user)
                    }
                }

                val userAdapter = userAdapter(userList)
                userRecyclerView.adapter = userAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"hello",Toast.LENGTH_SHORT).show()
            }

        })
    }

}