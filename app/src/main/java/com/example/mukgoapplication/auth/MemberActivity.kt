package com.example.mukgoapplication.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mukgoapplication.MainActivity
import com.example.mukgoapplication.R
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MemberActivity : AppCompatActivity() {

    lateinit var imgMemberProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        val etMemberNick = findViewById<EditText>(R.id.etMemberNick)
        val etMemberName = findViewById<EditText>(R.id.etMemberName)
        val etMemberIntro = findViewById<EditText>(R.id.etMemberIntro)
        imgMemberProfile = findViewById(R.id.imgMemberProfile)
        val btnMemberJoin = findViewById<Button>(R.id.btnMemberJoin)

        btnMemberJoin.setOnClickListener {

            val nick = etMemberNick.text.toString()
            val name = etMemberName.text.toString()
            val intro = etMemberIntro.text.toString()
            val uid = FBAuth.getUid()

            var key = FBDatabase.getMemberRef().child(uid).key.toString()
            FBDatabase.getMemberRef().child(key).setValue(MemberVO(nick, name, intro, uid))

            imgUpload(key)
            finish()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imgMemberProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            imgMemberProfile.setImageURI(it.data?.data)
        }
    }

    fun imgUpload(key: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

//          ImageView의 데이터 가져오기 api
        imgMemberProfile.isDrawingCacheEnabled = true
        imgMemberProfile.buildDrawingCache()
        val bitmap = (imgMemberProfile.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val data = baos.toByteArray()

        //            이미지 저장 경로 설정
        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
//                 실패 값 다루기
        }.addOnSuccessListener { taskSnapshot ->
//                 taskSnapshot.metadata contains file metadata such as size, content-type, etc.

        }


    }
}