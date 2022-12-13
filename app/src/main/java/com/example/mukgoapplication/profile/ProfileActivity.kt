package com.example.mukgoapplication.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.chat.ChatActivity
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.BoardVO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text

class ProfileActivity : AppCompatActivity() {

    lateinit var tvProfileIntro : TextView
    lateinit var tvProfileName : TextView
    lateinit var tvProfileNick : TextView

    lateinit var imgProfileProfile: ImageView

    lateinit var memberList : ArrayList<MemberVO>
    lateinit var boardList : ArrayList<BoardVO>

    lateinit var nick : String

    val keyData = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {

        getProfileData()

        val data = getProfileData()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgProfileProfile = findViewById(R.id.imgProfileProfile)
        tvProfileNick = findViewById<TextView>(R.id.tvProfileNick)
        tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        tvProfileIntro = findViewById<TextView>(R.id.tvProfileIntro)
        val btnProfileMessage = findViewById<Button>(R.id.btnProfileMessage)
        val rvProfileBoard = findViewById<RecyclerView>(R.id.rvProfileBoard)

        boardList = ArrayList()
        memberList = ArrayList()

        val database = Firebase.database

        val nick = database.getReference("nick").toString()
        val name = database.getReference("name").toString()
        val intro = database.getReference("intro").toString()

        val key = intent.getStringExtra("key")
        val uid = FBAuth.getUid()


        tvProfileNick.text = nick.toString()
        tvProfileName.text = name.toString()
        tvProfileIntro.text = intro.toString()

        val adapter = ProfileAdapter(this, boardList)

        rvProfileBoard.adapter = adapter
        rvProfileBoard.layoutManager = GridLayoutManager(this, 3,)

        btnProfileMessage.setOnClickListener {
            Log.d("uid", uid)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", uid)
            Log.d("key", data.toString())

            startActivity(intent)

        }

        getProfileImage(key.toString())




    }

    fun getProfileImage(key: String) {
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imgProfileProfile) // 지역변수
            }
        }
    }

    fun getProfileData(){
        val postListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                memberList.clear()
                for(model in snapshot.children){
                    val item = model.getValue(MemberVO::class.java)
                    if(item!=null){
                        memberList.add(item)
                    }
                    keyData.add(model.key.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        FBDatabase.getMemberRef().addValueEventListener(postListener)

    }

}