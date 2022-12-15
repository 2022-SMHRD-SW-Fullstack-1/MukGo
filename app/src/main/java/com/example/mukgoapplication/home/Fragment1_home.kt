package com.example.mukgoapplication.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.BoardVO
import com.example.mukgoapplication.write.WriteActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Fragment1_home : Fragment() {

    var bookmarkList = ArrayList<String>()
    var homeBoardList = ArrayList<BoardVO>()
    lateinit var adapter: HomeAdapter
    var keyData = ArrayList<String>()

    lateinit var bookmarkRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment1_home, container, false)
        val database = Firebase.database
        val rvHome = view.findViewById<RecyclerView>(R.id.rvHome)
        bookmarkRef  = database.getReference("bookList")

        getHomeBoardData()
        getBookMarkData()

        adapter = HomeAdapter(requireContext(), homeBoardList, keyData, bookmarkList)

        rvHome.adapter = adapter
        rvHome.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun getHomeBoardData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                homeBoardList.clear()
                for (model in snapshot.children) {
                    val item = model.getValue(BoardVO::class.java)
                    if (item != null) {
                        homeBoardList.add(item)
                    }
                    Log.d("key", model.key.toString())
                    keyData.add(model.key.toString())
                }
                homeBoardList.reverse()
                keyData.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getAllBoardRef().addValueEventListener(postListener)
    }

    fun getBookMarkData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookmarkList.clear()
                for (model in snapshot.children) {
                    val item = model.getValue() as String
                    if (item != null) {
                        bookmarkList.add(model.key.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getBookmarkRef().child(FBAuth.getUid()).addValueEventListener(postListener)
    }

}