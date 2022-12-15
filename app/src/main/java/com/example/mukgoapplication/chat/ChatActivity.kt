package com.example.mukgoapplication.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.databinding.ActivityChatBinding
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.CommentVO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var adapter: ChatAdapter
    val chatList = ArrayList<ChatVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = intent.getStringExtra("oppUid").toString()
        getUserData(uid, binding.tvChatNick, binding.imgChat)

        val chatroomKey = intent.getStringExtra("chatroomKey").toString()
        getChatData(chatroomKey)

        adapter = ChatAdapter(this, chatList)
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        binding.btnChatSend.setOnClickListener {
            val chat = ChatVO(binding.etChatMsg.text.toString(), FBAuth.getUid(), FBAuth.getTime())

            val key = FBDatabase.getChatRef(chatroomKey).push().key.toString()
            FBDatabase.getChatRef(chatroomKey).child(key).setValue(chat)

            Firebase.database.getReference("chatroom").child(chatroomKey).setValue(ChatRoomVO(FBAuth.getUid(), uid, binding.etChatMsg.text.toString(), FBAuth.getTime()))
            binding.etChatMsg.text = null
        }

    }

    fun getChatData(key: String){
        val postListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                // firebase에서 snapshot으로 데이터를 받아온 경우
                for(model in snapshot.children) {
                    val item = model.getValue(ChatVO::class.java) as ChatVO
                    chatList.add(item)
                    Log.d("comment", item.toString())
//                    keyData.add(model.key.toString())
                }
                chatList.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류가 발생했을 때 실행되는 함수
            }
        }
        FBDatabase.getChatRef(key).addValueEventListener(postListener)
    }

    fun getUserData(uid: String, tv: TextView, iv: ImageView){
        FBDatabase.database.getReference("member").child(uid).get().addOnSuccessListener {
            val item = it.getValue(MemberVO::class.java) as MemberVO
            tv.setText(item.nick)
            getImageData(item.uid, iv)

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun getImageData(key : String, view: ImageView){
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task->
            if (task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(view)
            }
        }
    }
}