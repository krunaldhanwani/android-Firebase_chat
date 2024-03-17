package com.example.firebasedemochat

data class chat(
    val senderId : String = "",
    val receiverId : String = "",
    val message : String = "",
    val videoUrl : String = "",
    val pdfFile : String = "",
    val imageUrl : String = "",
    val time : String = ""
)
