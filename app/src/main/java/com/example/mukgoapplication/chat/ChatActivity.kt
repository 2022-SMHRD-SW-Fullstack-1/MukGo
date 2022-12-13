package com.example.mukgoapplication.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mukgoapplication.databinding.ActivityChatBinding
import com.example.mukgoapplication.utils.FBAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatList = ArrayList<ChatVO>()

        val adapter = ChatAdapter(this, chatList)

        binding.btnChatSend.setOnClickListener {
            val chat = ChatVO(binding.etChatMsg.text.toString(), FBAuth.getUid(), FBAuth.getTime())
            binding.etChatMsg.text = null

            val chatroomUID = intent.getStringExtra("chatroomUID").toString()
            val chatroomRef = Firebase.database.getReference("chatroom")
            chatroomRef.child(chatroomUID).push().setValue(chat)
            chatList.add(chat)
            adapter.notifyDataSetChanged()
        }

    }
}