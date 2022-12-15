package com.example.mukgoapplication.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mukgoapplication.MainActivity
import com.example.mukgoapplication.R
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class ChatRoomFragment : Fragment() {

    lateinit var adapter: ChatRoomAdapter
    val chatList = ArrayList<ChatRoomVO>()
    val keyData = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)

        val rvChatRoom = view.findViewById<RecyclerView>(R.id.rvChatRoom)

        getChatRoomData()
        adapter = ChatRoomAdapter(requireContext(), chatList)
        adapter.setOnItemClickListener(object : ChatRoomAdapter.onItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(requireContext(),ChatActivity::class.java)
                var oppUid = chatList[position].uidOne
                if (oppUid == FBAuth.getUid())
                    oppUid = chatList[position].uidTwo
                intent.putExtra("oppUid", oppUid)
                intent.putExtra("chatroomKey", keyData[position])

                startActivity(intent)
            }
        })
        rvChatRoom.adapter = adapter
        rvChatRoom.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun getChatRoomData(){
        val postListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                // firebase에서 snapshot으로 데이터를 받아온 경우
                for(model in snapshot.children) {
                    val item = model.getValue(ChatRoomVO::class.java) as ChatRoomVO
                    if(item.uidOne == FBAuth.getUid() || item.uidTwo == FBAuth.getUid()) {
                        chatList.add(item)
                        keyData.add(model.key.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류가 발생했을 때 실행되는 함수
            }
        }
        FBDatabase.database.getReference("chatroom").addValueEventListener(postListener)
    }

}