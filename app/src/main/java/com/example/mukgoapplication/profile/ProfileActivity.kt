package com.example.mukgoapplication.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.BoardVO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileActivity : AppCompatActivity() {

    val memberData = ArrayList<MemberVO>()
    lateinit var adapter: ProfileAdapter
    val profileBoard = ArrayList<BoardVO>()
    val keyData = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        getProfileBoardData()

        val imgProfileProfile = findViewById<ImageView>(R.id.imgProfileProfile)
        val btnProfileMessage = findViewById<Button>(R.id.btnProfileMessage)
        val rvProfileBoard = findViewById<RecyclerView>(R.id.rvProfileBoard)
        val tvProfileNick = findViewById<TextView>(R.id.tvProfileNick)
        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileIntro = findViewById<TextView>(R.id.tvProfileIntro)

        val profileUid = intent.getStringExtra("uid").toString()
        getImageData(profileUid, imgProfileProfile)
        getMemberData(profileUid, tvProfileNick, tvProfileName, tvProfileIntro)

        adapter = ProfileAdapter(this, profileBoard, keyData)
        rvProfileBoard.adapter = adapter
        rvProfileBoard.layoutManager = GridLayoutManager(this, 3)

        if (profileUid == FBAuth.getUid())
            btnProfileMessage.setText("프로필 수정")

        btnProfileMessage.setOnClickListener {
            if (profileUid == FBAuth.getUid()) {
                // 프로필 수정 기능 구현
                val intent = Intent(this, ProfileEditActivity::class.java)
                intent.putExtra("uid", FBAuth.getUid())
                startActivity(intent)
            } else {
                // 채팅방 연결기능 구현

            }
        }


    }

    fun getMemberData(uid: String, nickV: TextView, nameV: TextView, introV: TextView) {
        FBDatabase.getMemberRef().child(uid).get().addOnSuccessListener {
            val item = it.getValue(MemberVO::class.java) as MemberVO
            nickV.setText(item.nick)
            nameV.setText(item.name)
            introV.setText(item.intro)
        }.addOnFailureListener {

        }

    }

    fun getImageData(key: String, view: ImageView) {
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(view)
            }
        }
    }

    fun getProfileBoardData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileBoard.clear()
                for (model in snapshot.children) {
                    val item = model.getValue(BoardVO::class.java)
                    if (item != null && item.uid == FBAuth.getUid()) {
                        profileBoard.add(item)
                        keyData.add(model.key.toString())
                    }
                }
                profileBoard.reverse()
                keyData.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getAllBoardRef().addValueEventListener(postListener)
    }

}