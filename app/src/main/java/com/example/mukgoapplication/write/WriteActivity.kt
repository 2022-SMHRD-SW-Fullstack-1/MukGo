package com.example.mukgoapplication.write

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mukgoapplication.R
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class WriteActivity : AppCompatActivity() {

    lateinit var ivWriteImage : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val etWriteContent = findViewById<EditText>(R.id.etWriteContent)
        ivWriteImage = findViewById(R.id.ivWriteImage)
        val btnWriteSubmit = findViewById<Button>(R.id.btnWriteSubmit)

        btnWriteSubmit.setOnClickListener {
            val content = etWriteContent.text.toString()

            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            var key = FBDatabase.getBoardRef().push().key.toString()
            FBDatabase.getBoardRef().child(key).setValue(BoardVO(content, uid, time))
            imgUpload(key)
            finish()
        }


        ivWriteImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }
    }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                ivWriteImage.setImageURI(it.data?.data)
            }
        }

    fun imgUpload(key: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

//          ImageView의 데이터 가져오기 api
        ivWriteImage.isDrawingCacheEnabled = true
        ivWriteImage.buildDrawingCache()
        val bitmap = (ivWriteImage.drawable as BitmapDrawable).bitmap
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