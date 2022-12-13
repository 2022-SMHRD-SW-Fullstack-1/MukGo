package com.example.mukgoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.mukgoapplication.chat.ChatRoomFragment
import com.example.mukgoapplication.databinding.ActivityMainBinding
import com.example.mukgoapplication.home.Fragment1_home
import com.example.mukgoapplication.profile.ProfileActivity
import com.example.mukgoapplication.setting.SettingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
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
}