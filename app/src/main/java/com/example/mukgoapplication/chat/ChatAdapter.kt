package com.example.mukgoapplication.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mukgoapplication.R
import com.example.mukgoapplication.utils.FBAuth

class ChatAdapter(val context: Context, val chatList: ArrayList<ChatVO>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMsgOpp: TextView
        val tvTimeOpp: TextView
        val tvMsgMy: TextView
        val tvTimeMy: TextView
        init {
            tvMsgOpp = itemView.findViewById(R.id.tvMsgOpp)
            tvTimeOpp = itemView.findViewById(R.id.tvTimeOpp)
            tvMsgMy = itemView.findViewById(R.id.tvMsgMy)
            tvTimeMy = itemView.findViewById(R.id.tvTimeMy)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.chat_msg_temp, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var time = FBAuth.myTime(chatList[position].time)

        if (chatList[position].uid == FBAuth.getUid()) {
            holder.tvMsgMy.setText(chatList[position].msg)
            holder.tvTimeMy.setText(time)
            holder.tvMsgOpp.isVisible = false
            holder.tvTimeOpp.isVisible = false
            holder.tvMsgMy.isVisible = true
            holder.tvTimeMy.isVisible = true

        } else {
            holder.tvMsgOpp.setText(chatList[position].msg)
            holder.tvTimeOpp.setText(time)
            holder.tvMsgMy.isVisible = false
            holder.tvTimeMy.isVisible = false
            holder.tvMsgOpp.isVisible = true
            holder.tvTimeOpp.isVisible = true
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}