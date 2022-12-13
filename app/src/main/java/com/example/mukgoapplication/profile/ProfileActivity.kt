package com.example.mukgoapplication.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.mukgoapplication.R
import com.example.mukgoapplication.chat.ChatActivity
import com.example.mukgoapplication.utils.FBAuth
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val imgProfileProfile = findViewById<ImageView>(R.id.imgProfileProfile)
        val tvProfileNick = findViewById<TextView>(R.id.tvProfileNick)
        val btnProfileMessage = findViewById<Button>(R.id.btnProfileMessage)

        val uid = FBAuth.getUid()

        if(intent.hasExtra("uid")){
            val uid = intent.getStringExtra("id")
        }



        btnProfileMessage.setOnClickListener {
            Log.d("uid", uid)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }









    }
}