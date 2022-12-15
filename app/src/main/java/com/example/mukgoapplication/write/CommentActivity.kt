package com.example.mukgoapplication.write

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mukgoapplication.databinding.ActivityCommentBinding
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CommentActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommentBinding
    lateinit var adapter: CommentAdapter
    val cmtList = ArrayList<CommentVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImageData(intent.getStringExtra("profileUid").toString(),binding.imgCmtProfile)
        binding.tvCmtNick.setText(intent.getStringExtra("nick"))
        binding.tvCmtContent.setText(intent.getStringExtra("content"))
        binding.tvCmtTime.setText(intent.getStringExtra("time"))

        // getCmt()를 호출해서 cmtList가져오기
        getCommentData(intent.getStringExtra("boardKey").toString())

        adapter = CommentAdapter(this, cmtList)
        binding.rvCmt.adapter = adapter
        binding.rvCmt.layoutManager = LinearLayoutManager(this)

        binding.btnCmtSend.setOnClickListener {
            // database에 저장
            val boardKey = intent.getStringExtra("boardKey").toString()
            var key =FBDatabase.getCommentRef(boardKey).push().key.toString()
            FBDatabase.getCommentRef(boardKey).child(key).setValue(CommentVO(binding.etCmt.text.toString(), FBAuth.getUid(), FBAuth.getTime()))
            binding.etCmt.setText("")
        }
    }

    fun getCommentData(key: String){
        val postListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cmtList.clear()
                // firebase에서 snapshot으로 데이터를 받아온 경우
                for(model in snapshot.children) {
                    val item = model.getValue(CommentVO::class.java) as CommentVO
                    cmtList.add(item)
                    Log.d("comment", item.toString())
//                    keyData.add(model.key.toString())
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류가 발생했을 때 실행되는 함수
            }
        }
        FBDatabase.getCommentRef(key).addValueEventListener(postListener)
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