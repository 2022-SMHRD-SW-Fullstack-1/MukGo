package com.example.mukgoapplication.write

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mukgoapplication.databinding.ActivityCommentBinding

class CommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}