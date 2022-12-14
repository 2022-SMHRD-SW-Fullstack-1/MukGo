package com.example.mukgoapplication.bookmark

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mukgoapplication.R
import com.example.mukgoapplication.home.HomeAdapter
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.BoardVO
import com.example.mukgoapplication.write.CommentAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Fragment_bookmark : Fragment() {

    var bookBoardList = ArrayList<BoardVO>()
    var bookmarkList = ArrayList<String>()
    lateinit var adapter: HomeAdapter
    var keyData = ArrayList<String>()
    var uid = FBAuth.getUid()
    var booklikeList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        val rvBookmark = view.findViewById<RecyclerView>(R.id.rvBookmark)

        getBookBookData()
        getBookBoardData()
        getBookLikeData()

        adapter = HomeAdapter(requireContext(), bookBoardList, keyData, bookmarkList, booklikeList)

        rvBookmark.adapter = adapter
        rvBookmark.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun getBookLikeData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booklikeList.clear()
                for (model in snapshot.children) {
                    val item = model.getValue() as String
                    if (item != null) {
                        booklikeList.add(model.key.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getLikeRef().child(uid).addValueEventListener(postListener)
    }

    fun getBookBoardData() {
        val posterListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookBoardList.clear()
                for (model in snapshot.children) {
                    val item = model.getValue(BoardVO::class.java)
                    // bookmarkList에 값이 채워져있어야함
                    if (item != null && bookmarkList.contains(model.key.toString())) {
                        bookBoardList.add(item)
//                      data 내가 북마크 찍은 게시물만 담긴다.
                        keyData.add(model.key.toString())
                    }

                }
//                adapter 새로 고침하기
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getAllBoardRef().addValueEventListener(posterListener)
    }

    fun getBookBookData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookmarkList.clear()
                for (model in snapshot.children) {
                    bookmarkList.add(model.key.toString())
                }
                adapter.notifyDataSetChanged()
                getBookBoardData()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getBookmarkRef().child(uid).addValueEventListener(postListener)
    }


}