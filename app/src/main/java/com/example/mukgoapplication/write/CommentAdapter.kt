package com.example.mukgoapplication.write

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.utils.FBDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.reflect.typeOf

class CommentAdapter(val context: Context, val cmtList: ArrayList<CommentVO>): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    lateinit var cmtUser: MemberVO

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgCmtTempProfile: ImageView
        val tvCmtTempNick: TextView
        val tvCmtTempTime: TextView
        val tvCmtTempContent: TextView
        val imgCmtTempLike: ImageView
        init {
            imgCmtTempProfile = itemView.findViewById(R.id.imgCmtTempProfile)
            tvCmtTempNick = itemView.findViewById(R.id.tvCmtTempNick)
            tvCmtTempTime = itemView.findViewById(R.id.tvCmtTempTime)
            tvCmtTempContent = itemView.findViewById(R.id.tvCmtTempContent)
            imgCmtTempLike = itemView.findViewById(R.id.imgCmtTempLike)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.comment_temp, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCmtTempContent.setText(cmtList[position].comment)
        holder.tvCmtTempTime.setText(cmtList[position].time)
        getUserNick(cmtList[position].uid, holder.tvCmtTempNick, holder.imgCmtTempProfile)
    }

    override fun getItemCount(): Int {
        return cmtList.size
    }

    fun getImageData(key : String, view: ImageView){
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task->
            if (task.isSuccessful){
                Glide.with(context)
                    .load(task.result)
                    .into(view)
            }
        }
    }

    fun getUserNick(uid: String, tv: TextView, iv: ImageView){
        FBDatabase.database.getReference("member").child(uid).get().addOnSuccessListener {
            val item = it.getValue(MemberVO::class.java) as MemberVO
            tv.setText(item.nick)
            getImageData(item.uid, iv)

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

}