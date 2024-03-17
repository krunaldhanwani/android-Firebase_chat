package com.example.firebasedemochat

data class user(
    val userId : String = "",
    val userName : String = "",
    val lastMassage : String = "",
    val profileImage : String = "",
    val online : String = "",
    val typing : String = ""
)
