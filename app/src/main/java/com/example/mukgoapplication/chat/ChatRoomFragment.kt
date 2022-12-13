package com.example.mukgoapplication.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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


class ChatRoomFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)

        val rvChatRoom = view.findViewById<RecyclerView>(R.id.rvChatRoom)

        val chatList = ArrayList<ChatRoomVO>()



        val adapter = ChatRoomAdapter(requireContext(), chatList)
        adapter.setOnItemClickListener(object : ChatRoomAdapter.onItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(requireContext(),ChatActivity::class.java)
                startActivity(intent)
            }
        })
        rvChatRoom.adapter = adapter
        rvChatRoom.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

}