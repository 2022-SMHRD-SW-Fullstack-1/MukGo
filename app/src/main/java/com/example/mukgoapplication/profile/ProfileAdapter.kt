package com.example.mukgoapplication.profile

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.write.BoardVO
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileAdapter(val context: Context, val profileBoardList: ArrayList<BoardVO>) :
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    lateinit var imgProfileBoard : ImageView

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgProfileBoard: ImageView


        init {
            imgProfileBoard = itemView.findViewById(R.id.imgProfileBoard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.profile_board_template, null)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(profileBoardList[position].image).into(holder.imgProfileBoard)
    }

    override fun getItemCount(): Int {
        return profileBoardList.size
    }

    fun getImageData(key:String){
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener {
            task->
            if(task.isSuccessful){
                Glide.with(context)
                    .load(task.result)
                    .into(imgProfileBoard)
            }
        }
    }

}