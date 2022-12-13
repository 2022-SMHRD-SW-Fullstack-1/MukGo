package com.example.mukgoapplication.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mukgoapplication.R
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.BoardVO
import com.example.mukgoapplication.write.WriteActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Fragment1_home : Fragment() {

    var homeBoardList = ArrayList<BoardVO>()
    lateinit var adapter: HomeAdapter
    val keyData = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment1_home, container, false)

        val rvHome = view.findViewById<RecyclerView>(R.id.rvHome)

        val ivHomeWrite = view.findViewById<ImageView>(R.id.ivHomeWrite)

        ivHomeWrite.setOnClickListener {
            val intent = Intent(context, WriteActivity::class.java)
            startActivity(intent)
        }

        getHomeBoardData()

        adapter = HomeAdapter(requireContext(), homeBoardList)

        rvHome.adapter = adapter
        rvHome.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun getHomeBoardData(){
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(model in snapshot.children){
                    val item = model.getValue(BoardVO::class.java)
                    if(item !=null){
                        homeBoardList.add(item)
                    }
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
        FBDatabase.getBoardRef().addValueEventListener(postListener)
    }

}