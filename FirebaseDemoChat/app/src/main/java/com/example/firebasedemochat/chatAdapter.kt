package com.example.firebasedemochat

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.layout_user.view.*
import kotlinx.android.synthetic.main.msg_design.view.*
import java.util.*
import kotlin.collections.ArrayList

class chatAdapter(private var chatList: ArrayList<chat>,private var callBack: CallBack,private var type : String) : RecyclerView.Adapter<chatAdapter.ViewHolder>() {
    var firebaseUser : FirebaseUser? = null

    lateinit var storeUserData: StoreUserData

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_design,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        storeUserData = StoreUserData(holder.itemView.context)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        holder.itemView.ivReceiver.visibility = View.GONE
        holder.itemView.ivSender.visibility = View.GONE
        holder.itemView.tvSender.visibility = View.GONE
        holder.itemView.tvReceiver.visibility = View.GONE
        holder.itemView.Receiver.visibility = View.GONE
        holder.itemView.Sender.visibility = View.GONE
        holder.itemView.ivReceiverPdf.visibility = View.GONE
        holder.itemView.ivSenderPdf.visibility = View.GONE
        chat(holder, position)

        var lastMassage = chatList[itemCount - 1].message
        storeUserData.setString(Constant.LastMassage,lastMassage)
        Constant.Id = chatList[position].receiverId
        Constant.LastMassage = lastMassage
        callBack.lastMassage(lastMassage , chatList[position].receiverId)

            holder.itemView.ivSender.setOnClickListener {
                if (!chatList[position].imageUrl.isNullOrEmpty()) {
                    callBack.photoLink(chatList[position].imageUrl,"image")
                } else if(!chatList[position].videoUrl.isNullOrEmpty()) {
                    callBack.photoLink(chatList[position].videoUrl, "video")
                } else {
                    callBack.photoLink(chatList[position].pdfFile, "pdf")
                }
            }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun chat(holder:ViewHolder, position : Int) {
        Log.e("chatListchatList",""+chatList[position].time)
        if (chatList[position].senderId == firebaseUser!!.uid) {
            holder.itemView.tvSenderTime.text = chatList[position].time
            holder.itemView.Sender.visibility = View.VISIBLE
            if (type == "image") {
                if (!chatList[position].imageUrl.isNullOrEmpty()) {
                    holder.itemView.ivSender.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].imageUrl)
                        .into(holder.itemView.ivSender)
                }else if(!chatList[position].videoUrl.isNullOrEmpty()) {
                    holder.itemView.ivSender.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].videoUrl)
                        .into(holder.itemView.ivSender)
                }else if(!chatList[position].pdfFile.isNullOrEmpty()) {
                    holder.itemView.ivSenderPdf.visibility = View.VISIBLE
                } else {
                    holder.itemView.tvSender.visibility = View.VISIBLE
                    holder.itemView.tvSender.text = chatList[position].message
                }
            } else {
                if (!chatList[position].videoUrl.isNullOrEmpty()) {
                    holder.itemView.ivSender.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].videoUrl)
                        .into(holder.itemView.ivSender)
                }else if(!chatList[position].imageUrl.isNullOrEmpty()) {
                    holder.itemView.ivSender.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].imageUrl)
                        .into(holder.itemView.ivSender)
                }else if(!chatList[position].pdfFile.isNullOrEmpty()) {
                    holder.itemView.ivSenderPdf.visibility = View.VISIBLE
                } else {
                    holder.itemView.tvSender.visibility = View.VISIBLE
                    holder.itemView.tvSender.text = chatList[position].message
                }
            }
        } else {
            holder.itemView.tvReceiverTime.text =  chatList[position].time
            holder.itemView.Receiver.visibility = View.VISIBLE
            if (type == "image") {
                if (!chatList[position].imageUrl.isNullOrEmpty()) {
                    holder.itemView.ivReceiver.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].imageUrl)
                        .into(holder.itemView.ivReceiver)
                } else if (!chatList[position].videoUrl.isNullOrEmpty()) {
                    holder.itemView.ivReceiver.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].videoUrl)
                        .into(holder.itemView.ivReceiver)
                }else if(!chatList[position].pdfFile.isNullOrEmpty()) {
                    holder.itemView.ivReceiverPdf.visibility = View.VISIBLE
                } else {
                        holder.itemView.tvReceiver.visibility = View.VISIBLE
                        holder.itemView.tvReceiver.text = chatList[position].message
                    }
            } else {
                if (!chatList[position].videoUrl.isNullOrEmpty()) {
                    holder.itemView.ivReceiver.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].videoUrl)
                        .into(holder.itemView.ivReceiver)
                } else if (!chatList[position].imageUrl.isNullOrEmpty()) {
                    holder.itemView.ivReceiver.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(chatList[position].imageUrl)
                        .into(holder.itemView.ivReceiver)
                }else if(!chatList[position].pdfFile.isNullOrEmpty()) {
                    holder.itemView.ivReceiverPdf.visibility = View.VISIBLE
                } else {
                    holder.itemView.tvReceiver.visibility = View.VISIBLE
                    holder.itemView.tvReceiver.text = chatList[position].message
                }
            }
        }
    }
}