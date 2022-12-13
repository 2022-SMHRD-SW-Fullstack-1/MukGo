package com.example.mukgoapplication.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.chat.ChatActivity
import com.example.mukgoapplication.utils.FBAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileActivity : AppCompatActivity() {

    lateinit var imgProfileProfile : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgProfileProfile = findViewById(R.id.imgProfileProfile)
        val tvProfileNick = findViewById<TextView>(R.id.tvProfileNick)
        val btnProfileMessage = findViewById<Button>(R.id.btnProfileMessage)

        val nick = intent.getStringExtra("nick")

        val key = FirebaseAuth.getInstance().uid.toString()
        getProfileData(key)

        val uid = FBAuth.getUid()

        tvProfileNick.text = nick.toString()

        btnProfileMessage.setOnClickListener {
            Log.d("uid", uid)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }


    }

    fun getProfileData(key:String){
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task->
            if(task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(imgProfileProfile) // 지역변수
            }
        }
    }


}