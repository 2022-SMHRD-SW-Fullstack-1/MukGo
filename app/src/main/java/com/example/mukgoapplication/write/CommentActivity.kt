package com.example.mukgoapplication.write

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.databinding.ActivityCommentBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImageData(intent.getStringExtra("profileUid").toString(),binding.imgCmtProfile)
        binding.tvCmtNick.setText(intent.getStringExtra("nick"))
        binding.tvCmtContent.setText(intent.getStringExtra("content"))
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