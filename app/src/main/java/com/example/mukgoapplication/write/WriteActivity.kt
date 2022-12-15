package com.example.mukgoapplication.write

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.UriPermission
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.provider.MediaStore.Audio.Media
import android.util.Log.d
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.VolleyLog.d

import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.google.android.datatransport.runtime.logging.Logging.d
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.logging.Logger

class WriteActivity : AppCompatActivity() {

    lateinit var ivWriteImage : ImageView
    lateinit var ivWriteCamera: ImageView
    lateinit var etWriteContent:EditText

    var nick = ""
//    카메라

    val cameraPermission = arrayOf(Manifest.permission.CAMERA)
    val storagePermission = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //    권한 플래그 값
    val flagPermCamera = 98
    val flagPermStorage = 99

    //    카메라와 갤러리 호출 플래그
    val flagReqCamera = 101
    val flagReaStorage = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        etWriteContent = findViewById<EditText>(R.id.etWriteContent)
        ivWriteImage = findViewById(R.id.ivWriteImage)
        ivWriteCamera = findViewById(R.id.ivWriteCamera)

        checkImage()

        val btnWriteSubmit = findViewById<Button>(R.id.btnWriteSubmit)

//        카메라


        if (checkPermission(storagePermission, flagPermStorage)) {
            setViews()
        }

        getUserNick(FBAuth.getUid())

//        글쓰기


        btnWriteSubmit.setOnClickListener {
            val content = etWriteContent.text.toString()

            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            var key2 = FBDatabase.getAllBoardRef().push().key.toString()
            FBDatabase.getAllBoardRef().child(key2).setValue(BoardVO(content, uid, time, nick))

            imgUpload(key2)
            cameraUpload(key2)

            finish()
        }


        ivWriteImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            checkImage()
            launcher.launch(intent)
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            ivWriteImage.setImageURI(it.data?.data)
        }
    }

//    이미지 업로드

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

//    사진

    fun setViews() {
        ivWriteCamera.setOnClickListener {
            openCamera()
        }

    }

    fun openCamera() {
        if (checkPermission(cameraPermission, flagPermCamera)) {
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, flagReqCamera)
            checkImage()
        }
    }

    //    권한승인
    fun checkPermission(permissions: Array<out String>, flag: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }
        return true
    }

    //    사용자의 권한 허용 여부 선택 메소드 값 전달
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            flagPermStorage -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해야 사진을 불러옵니다.", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                }
                setViews()
            }
            flagPermCamera -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해야 촬영이 가능합니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                openCamera()
            }
        }
    }

//    결과값을 해당 메소드로 호출

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                flagReqCamera -> {
                    if (data?.extras?.get("data") != null) {
//                        촬영 사진을 이미지 뷰로 전달
                        val bitmap = data?.extras?.get("data") as Bitmap
                        ivWriteCamera.setImageBitmap(bitmap)
                    }
                }
            }
        }

    }

    //    이미지 업로드

    fun cameraUpload(key: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

//          ImageView의 데이터 가져오기 api
        ivWriteCamera.isDrawingCacheEnabled = true
        ivWriteCamera.buildDrawingCache()
        val bitmap = (ivWriteCamera.drawable as BitmapDrawable).bitmap
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

    fun checkImage(){
        Log.d("imagedata", ivWriteImage.toString())
        Log.d("imagedata", ivWriteCamera.toString())
    }

}