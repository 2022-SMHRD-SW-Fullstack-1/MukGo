package com.example.mukgoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.mukgoapplication.home.Fragment1_home
import com.example.mukgoapplication.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flMain = findViewById<FrameLayout>(R.id.flMain)
        val ivProfile = findViewById<ImageView>(R.id.ivProfile)
        val ivAlarm = findViewById<ImageView>(R.id.ivAlarm)
        val ivSetting = findViewById<ImageView>(R.id.ivSetting)
        val bnvMenu = findViewById<BottomNavigationView>(R.id.bnvMenu)

        ivProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
        }

        supportFragmentManager.beginTransaction().replace(
            R.id.flMain,
            Fragment1_home()
        ).commit()

        bnvMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tap1 -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.flMain,
                        Fragment1_home()
                    ).commit()
                }
            }
            true
        }


    }
}