package com.example.mukgoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.chat.ChatRoomFragment
import com.example.mukgoapplication.databinding.ActivityMainBinding
import com.example.mukgoapplication.home.Fragment1_home
import com.example.mukgoapplication.profile.ProfileActivity
import com.example.mukgoapplication.setting.SettingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var imgContent: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgContent = binding.ivProfile
        val key = FirebaseAuth.getInstance().uid.toString()
        getImageData(key)

        binding.ivProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        supportFragmentManager.beginTransaction().replace(
            R.id.flMain,
            Fragment1_home()
        ).commit()

        binding.bnvMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tap1 -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.flMain,
                        Fragment1_home()
                    ).commit()
                }
                R.id.tap2 -> {

                }
                R.id.tap3 -> {

                }
                R.id.tap4 -> {

                }
                R.id.tap5 -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.flMain,
                        ChatRoomFragment()
                    ).commit()
                }
            }
            true
        }

    }

    fun getImageData(key: String) {
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imgContent)

            }
        }
    }
}