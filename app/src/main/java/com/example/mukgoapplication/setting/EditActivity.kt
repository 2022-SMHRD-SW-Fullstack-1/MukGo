package com.example.mukgoapplication.setting

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mukgoapplication.databinding.ActivityEditBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            binding.etEditEmail.setText(email.toString())
            Log.d("auth:name", name.toString())
            Log.d("auth:email", email.toString())
            Log.d("auth:photoUrl", photoUrl.toString())

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
        }

        binding.btnEdit.setOnClickListener {
            val newPassword = binding.etEditPw.text.toString()

            val editor = getSharedPreferences("autoLogin", Context.MODE_PRIVATE).edit()
            editor.putString("loginPw", newPassword)
            editor.commit()

            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "User password updated.")
                    }
                }

            finish()
        }

    }
}