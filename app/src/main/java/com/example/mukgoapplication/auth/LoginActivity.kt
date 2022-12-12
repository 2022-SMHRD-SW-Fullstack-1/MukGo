package com.example.mukgoapplication


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mukgoapplication.auth.JoinActivity
import com.example.mukgoapplication.utils.FBAuth.Companion.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var etJoin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // SharedPreferences가 다르면 상관없음 이름!!
        val sharePreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        val loginId = sharePreferences.getString("loginId", "")
        val loginPw = sharePreferences.getString("loginPw", "")

        val sp = getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
        val loginName = sp.getString("loginId", "null")

        // FirebaseAuth 초기화
        auth = Firebase.auth

        val etLoginId = findViewById<EditText>(R.id.etLoginId)
        val etLoginPw = findViewById<EditText>(R.id.etLoginPw)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
         etJoin = findViewById(R.id.etJoin)
        etLoginId.setText(loginId)
        etLoginPw.setText(loginPw)

        etJoin.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        // login 버튼을 눌렀을 때
        btnLogin.setOnClickListener {
            val email = etLoginId.text.toString()
            val pw = etLoginPw.text.toString()

            auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인에 성공
                        Toast.makeText(
                            this, "로그인 성공",
                            Toast.LENGTH_SHORT
                        ).show()
                        val editor = sharePreferences.edit()
                        editor.putString("loginId", email)
                        editor.putString("loginPw", pw)
                        editor.commit()

                        val editorSp = sp.edit()
                        editorSp.putString("loginId", email)
                        editorSp.commit()


                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 로그인에 실패
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }

            Toast.makeText(this@LoginActivity, "$email,$pw", Toast.LENGTH_SHORT).show()
        }
    }
}





