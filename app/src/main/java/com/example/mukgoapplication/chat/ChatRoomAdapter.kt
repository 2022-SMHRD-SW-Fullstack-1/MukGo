package com.example.mukgoapplication.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatRoomAdapter(val context: Context, val chatroomList: ArrayList<ChatRoomVO>): RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    interface onItemClickListener{
        fun onItemClick(view: View, position: Int)
    }

    lateinit var monItemClickListener: onItemClickListener

    fun setOnItemClickListener(onItemClickListener: onItemClickListener) {
        monItemClickListener = onItemClickListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCRT: ImageView
        val tvCRTNick: TextView
        val tvCRTLastMsg: TextView
        val tvCRTLastMsgTime: TextView
        init {
            imgCRT = itemView.findViewById(R.id.imgCRT)
            tvCRTNick = itemView.findViewById(R.id.tvCRTNick)
            tvCRTLastMsg = itemView.findViewById(R.id.tvCRTLastMsg)
            tvCRTLastMsgTime = itemView.findViewById(R.id.tvCRTLastMsgTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.chat_room_temp, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uid = chatroomList[position].opponentUID
        val img = Firebase.storage.reference.child("$uid.png")
        Glide.with(context).load(img).into(holder.imgCRT)

        val nick = Firebase.database.getReference("").child(uid).get()
        holder.tvCRTNick.setText(nick.toString())

        holder.tvCRTLastMsg.setText(chatroomList[position].lastChatMsg)
        holder.tvCRTLastMsgTime.setText(chatroomList[position].lastChatTime)
    }

    override fun getItemCount(): Int {
        return chatroomList.size
    }
}