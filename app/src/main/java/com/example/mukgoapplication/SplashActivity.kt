package com.example.mukgoapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val sharePreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
            val loginId = sharePreferences.getString("loginId", "")
            val loginPw = sharePreferences.getString("loginPw", "")
            if (loginId == "" && loginPw == "") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val auth = Firebase.auth
                auth.signInWithEmailAndPassword(loginId!!, loginPw!!)
                    .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인에 성공
                        Toast.makeText(
                            this, "로그인 성공",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 로그인에 실패
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }, 1100)
    }
}