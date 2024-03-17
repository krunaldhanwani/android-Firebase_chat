package com.example.firebasedemochat

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class userAdapter(private var userList: ArrayList<user>) : RecyclerView.Adapter<userAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView = view.findViewById(R.id.imageView)
        val tvTextView : TextView = view.findViewById(R.id.tvTextView)
        val tvMsg : TextView = view.findViewById(R.id.tvMsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val  user = userList[position]
        Log.e("userList" , "" + userList)
        holder.tvTextView.text = user.userName
        holder.tvMsg.text = user.lastMassage
        Glide.with(holder.itemView.context)
            .load(user.profileImage)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context ,ChatActivity::class.java)
                .putExtra("userID",user.userId)
                .putExtra("image",user.profileImage))
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}