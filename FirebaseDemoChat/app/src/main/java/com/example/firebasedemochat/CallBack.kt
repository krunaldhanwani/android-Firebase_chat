package com.example.firebasedemochat

interface CallBack {
    fun photoLink(photo : String , type : String) { }

    fun lastMassage(msg : String , type : String) { }
}