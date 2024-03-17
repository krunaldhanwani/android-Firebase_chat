package com.example.firebasedemochat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() , CallBack {
    var chatList = ArrayList<chat>()
    var firebaseUser: FirebaseUser? = null
    private var reference: DatabaseReference? = null
    private var reference2: DatabaseReference? = null

    var storageReference: StorageReference? = null
    private var imageLink: Uri? = null
    private var videoLink: Uri? = null
    private var pdfLink: Uri? = null

    lateinit var chatAdapter: chatAdapter

    var type = ""

    var userId = ""

    var userName = ""

    var receiverId = ""

    private lateinit var storage: FirebaseStorage

    private val PICK_IMAGE_REQUEST: Int = 2020

    private val PICK_VIDEO_REQUEST: Int = 1000

    private val PICK_PDF_REQUEST: Int = 5000

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val LayoutManager = LinearLayoutManager(this)
        LayoutManager.stackFromEnd = true
        rvSenderReceiverChat.layoutManager = LayoutManager


        var image = intent.getStringExtra("image")

        Glide.with(this@ChatActivity)
            .load(image)
            .into(chatUserProfile)


        userId = intent.getStringExtra("userID").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        checkStatus(firebaseUser!!.uid)
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        etMessageSend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    updateTyping("false")
                } else {
                    updateTyping(firebaseUser!!.uid)
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val user = snapshot.getValue(user::class.java)
                        tvBroadcastMemberName.text = user!!.userName
                        userName = user.userName
                        intent.getStringExtra("image")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })

        readMassage(firebaseUser!!.uid, userId)

        ivSmile.setOnClickListener {
            val sdf = SimpleDateFormat("HH:mm")
            val formattedDate = sdf.format(Date())
            var message: String = etMessageSend.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "hello", Toast.LENGTH_SHORT).show()
            } else {
                sendMassage(firebaseUser!!.uid, userId, message, "", "","",formattedDate)
                etMessageSend.setText("")
                chatAdapter.notifyDataSetChanged()
                rvSenderReceiverChat.smoothScrollToPosition(rvSenderReceiverChat.adapter!!.itemCount)
            }
        }

        ivAttachDoc.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Image"), PICK_IMAGE_REQUEST)
        }

        ivCamera.setOnClickListener {
            var intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Video"), PICK_VIDEO_REQUEST)
        }

        ivPdf.setOnClickListener {
            var intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Pdf"), PICK_PDF_REQUEST)
        }

    }

    private fun sendMassage(senderId: String, receiverId: String, message: String, ImageURl: String,pdfURl: String, VideoUrl: String,formattedDate :String) {
        reference = FirebaseDatabase.getInstance().reference

        var hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["imageUrl"] = ImageURl
        hashMap["videoUrl"] = VideoUrl
        hashMap["pdfFile"] =pdfURl
        hashMap["message"] = message
        hashMap["time"] = formattedDate

        reference!!.child("Chat").push().setValue(hashMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageLink = data!!.data
                type = "image"
                var auth = FirebaseAuth.getInstance()
                var user: FirebaseUser? = auth.currentUser

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

                readMassage(firebaseUser!!.uid, userId)
                addImageMessage("image",imageLink.toString())

            } else if (requestCode == PICK_PDF_REQUEST) {
                pdfLink = data!!.data
                type ="Pdf"
                var auth = FirebaseAuth.getInstance()
                var user: FirebaseUser? = auth.currentUser
                storageReference = storageReference!!.child(UUID.randomUUID().toString())

                storageReference!!.putFile(pdfLink!!)
                    .addOnSuccessListener(
                        OnSuccessListener<UploadTask.TaskSnapshot?> {
                            Toast.makeText(this, "Pdf Uploaded!!", Toast.LENGTH_SHORT).show()
                        })
                    .addOnFailureListener(OnFailureListener { e -> // Err
                        // or, Image not uploaded
                        Toast.makeText(this, "Failure$e", Toast.LENGTH_SHORT).show()
                    })
                    .addOnProgressListener(
                        OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        })


                Log.e("userId", " " + firebaseUser!!.uid)
                Log.e("userId2", " $userId")

                readMassage(firebaseUser!!.uid, userId)
                addImageMessage("Pdf",pdfLink.toString())
            }
            else {
                    videoLink = data!!.data
                    type = "video"
                    var auth = FirebaseAuth.getInstance()
                    var user: FirebaseUser? = auth.currentUser

                    storageReference = storageReference!!.child(UUID.randomUUID().toString())

                    storageReference!!.putFile(videoLink!!)
                        .addOnSuccessListener(
                            OnSuccessListener<UploadTask.TaskSnapshot?> {
                                Toast.makeText(this, "Video Uploaded!!", Toast.LENGTH_SHORT).show()
                            })
                        .addOnFailureListener(OnFailureListener { e -> // Err
                            // or, Image not uploaded
                            Toast.makeText(this, "Failure$e", Toast.LENGTH_SHORT).show()
                        })
                        .addOnProgressListener(
                            OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                            })

                    Log.e("userId", " " + firebaseUser!!.uid)
                    Log.e("userId2", " $userId")

                    readMassage(firebaseUser!!.uid, userId)
                    addImageMessage("video",videoLink.toString())
                }
            }
        }

    private fun readMassage(senderId: String, receiverId: String) {
        var databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(chat::class.java)


                    if (chat!!.senderId == senderId && chat.receiverId == receiverId || chat.senderId == receiverId && chat.receiverId == senderId) {
                        chatList.add(chat)
                        this@ChatActivity.receiverId = chat.receiverId
                        Log.e("chat", "" + chatList)
                        Log.e("chat2", "" + chat!!.time)
                    }
                }
                chatAdapter = chatAdapter(chatList, this@ChatActivity, type)
                rvSenderReceiverChat.adapter = chatAdapter
                rvSenderReceiverChat.smoothScrollToPosition(rvSenderReceiverChat.adapter!!.itemCount)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "hello", Toast.LENGTH_SHORT).show()
            }


    })
    }

    private fun addImageMessage(type: String,url : String) {
        val sdf = SimpleDateFormat("HH:mm")
        val formattedDate = sdf.format(Date())
        Log.e("formattedDate", formattedDate.toString())
        val metadata = StorageMetadata.Builder()
        metadata.setCustomMetadata("contentType", "image/jpeg")
        Log.e("Firebase-MediaUri55", metadata.toString())
        val strURL = FirebaseStorage.getInstance().getReference("Images")
            .child(url+ "TestImage")
            .putFile(Uri.parse(url), metadata.build())
        Log.e("Firebase-MediaUri", strURL.toString())
        strURL.addOnSuccessListener {
            Log.e("Firebase-MediaUri",it.toString())
            FirebaseStorage.getInstance().getReference("Images")
                .child(url + "TestImage").downloadUrl.addOnSuccessListener {
                    if (type == "image") {
                        sendMassage(firebaseUser!!.uid, userId, "", it.toString(),"","",formattedDate)
                    } else if (type == "Pdf") {
                        sendMassage(firebaseUser!!.uid, userId, "", "",it.toString(),"",formattedDate)
                    } else {
                        sendMassage(firebaseUser!!.uid, userId, "", "","",it.toString(),formattedDate)
                    }
            }.addOnFailureListener {
                Log.e("Firebase", "Failed in downloading")
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail $it")
        }

    }

    private fun updateOnline(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["online"] = status
        reference!!.updateChildren(hashMap)
    }

    private fun lastMassage(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["lastMassage"] = status
        reference!!.updateChildren(hashMap)
    }

    override fun lastMassage(msg: String, type: String) {
        super.lastMassage(msg, type)
        if (type == receiverId) {
            lastMassage(msg)
        }
    }

    private fun updateTyping(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["typing"] = status
        reference!!.updateChildren(hashMap)
    }

    private fun checkStatus(hisId: String) {
        reference2 = FirebaseDatabase.getInstance().getReference("Users").child(hisId)

        reference2!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val user = snapshot.getValue(user::class.java)
                        if (user!!.typing == userId) {
                            tvBroadcastMemberOnline.text = "typing....."
                        } else {
                            tvBroadcastMemberOnline.text = user.online
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })
    }

    override fun onResume() {
        updateOnline("online")
        super.onResume()
    }

    override fun onPause() {
        updateOnline("offline")
        super.onPause()
    }

    override fun photoLink(photo: String,type: String) {
        startActivity(Intent(this, ImageVideoActivity::class.java)
            .putExtra("image",photo)
            .putExtra("Type",type))
    }

}