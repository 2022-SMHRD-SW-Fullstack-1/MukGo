package com.example.mukgoapplication.write

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class WriteActivity : AppCompatActivity() {

    lateinit var ivWriteImage : ImageView
    lateinit var etWriteContent:EditText
    var nick = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val etWriteContent = findViewById<EditText>(R.id.etWriteContent)
        ivWriteImage = findViewById(R.id.ivWriteImage)
        val btnWriteSubmit = findViewById<Button>(R.id.btnWriteSubmit)

        getUserNick(FBAuth.getUid())

        var boardKey = intent.getStringExtra("boardKey").toString()
        if(boardKey!=null){
            getBoardData(boardKey, etWriteContent, ivWriteImage)
        }
        btnWriteSubmit.setOnClickListener {
            val content = etWriteContent.text.toString()

            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            var key2 = FBDatabase.getAllBoardRef().child(uid).push().key.toString()
            FBDatabase.getAllBoardRef().child(key2).setValue(BoardVO(content, uid, time, nick))
            if (boardKey!=null){
                key2=boardKey
                FBDatabase.getAllBoardRef().child(key2).setValue(BoardVO(content, uid, time))
            }
            FBDatabase.getAllBoardRef().child(key2).setValue(BoardVO(content, uid, time))
            imgUpload(key2)

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

    fun getUserNick(uid: String){
        FBDatabase.database.getReference("member").child(uid).get().addOnSuccessListener {
            val item = it.getValue(MemberVO::class.java) as MemberVO
            nick = item.nick
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }

    /**board 에 있는 데이터 전부를 가져오는 작업을 할 것*/
    fun getBoardData(uid: String, et: EditText, iv: ImageView){
        FBDatabase.getAllBoardRef().child(uid).get().addOnSuccessListener {
            val item = it.getValue(BoardVO::class.java)
            if (item != null) {
                et.setText(item.content)
                getImageData(item.uid,iv)
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun getImageData(key : String, view: ImageView){
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task->
            if (task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(view)
            }
        }
    }
}