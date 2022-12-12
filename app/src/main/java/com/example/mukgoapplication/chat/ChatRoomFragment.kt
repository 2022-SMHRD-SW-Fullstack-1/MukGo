package com.example.mukgoapplication.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        rvChatRoom.adapter = adapter
        rvChatRoom.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

}