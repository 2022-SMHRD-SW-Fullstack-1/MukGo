package com.example.mukgoapplication.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileEditActivity : AppCompatActivity() {

    lateinit var imgEditProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        imgEditProfile = findViewById(R.id.imgEditProfile)
        val etEditName = findViewById<TextView>(R.id.etEditName)
        val etEditNick = findViewById<TextView>(R.id.etEditNick)
        val etEditIntro = findViewById<TextView>(R.id.etEditIntro)
        val imgEditCancel = findViewById<ImageView>(R.id.imgEditCancel)
        val imgEditCheck = findViewById<ImageView>(R.id.imgEditCheck)

        val uid = intent.getStringExtra("uid").toString()
        getMemberData(uid, etEditName as EditText, etEditNick as EditText, etEditIntro as EditText)
        getMemberImage(uid, imgEditProfile)

        imgEditCancel.setOnClickListener {
            finish()
        }

        imgEditCheck.setOnClickListener {
            val nick = etEditName.text.toString()
            val name = etEditNick.text.toString()
            val intro = etEditIntro.text.toString()

            var key = FBDatabase.getMemberRef().child(uid).key.toString()
            FBDatabase.getMemberRef().child(key).setValue(MemberVO(nick, name, intro))

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)

        }

        imgEditProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }

    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            imgEditProfile.setImageURI(it.data?.data)
        }
    }


    fun getMemberData(uid: String, nickV: EditText, nameV: EditText, introV: EditText) {
        FBDatabase.getMemberRef().child(uid).get().addOnSuccessListener {
            val item = it.getValue(MemberVO::class.java) as MemberVO
            nickV.setText(item.nick)
            nameV.setText(item.name)
            introV.setText(item.intro)
        }.addOnFailureListener {

        }

    }

    fun getMemberImage(key: String, view: ImageView) {
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(view)
            }
        }

    }

    //    이미지 업로드

    fun imgUpload(key: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

//          ImageView의 데이터 가져오기 api
        imgEditProfile.isDrawingCacheEnabled = true
        imgEditProfile.buildDrawingCache()
        val bitmap = (imgEditProfile.drawable as BitmapDrawable).bitmap
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