package com.example.mukgoapplication.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mukgoapplication.R
import com.example.mukgoapplication.auth.MemberVO
import com.example.mukgoapplication.profile.ProfileActivity
import com.example.mukgoapplication.utils.FBAuth
import com.example.mukgoapplication.utils.FBDatabase
import com.example.mukgoapplication.write.BoardVO
import com.example.mukgoapplication.write.CommentActivity

import com.example.mukgoapplication.write.UpdateActivity
import com.example.mukgoapplication.write.WriteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.reflect.Member

class HomeAdapter(
    val context: Context,
    val boardHomeList: ArrayList<BoardVO>,
    val memberHomeList : ArrayList<MemberVO>,
    val keyData: ArrayList<String>
) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHomeContent: ImageView
        val imgHomeProfile: ImageView
        val tvHomeNick: TextView
        val imgHomeLike: ImageView
        val imgHomeComment: ImageView
        val imgHomeBookmark: ImageView
        val tvHomeLikeNum: TextView
        val tvHomeContent: TextView
        val tvHomeTime: TextView
        val btnHomeProfileMove: Button

        val uid = FBAuth.getUid()
        val route = FBDatabase.getBoardRef().child(uid)

        init {
            imgHomeProfile = itemView.findViewById(R.id.imgHomeProfile)
            tvHomeNick = itemView.findViewById(R.id.tvHomeNick)
            imgHomeContent = itemView.findViewById(R.id.imgHomeContent)
            imgHomeLike = itemView.findViewById(R.id.imgHomeLike)
            imgHomeComment = itemView.findViewById(R.id.imgHomeComment)
            imgHomeBookmark = itemView.findViewById(R.id.imgHomeBookmark)
            tvHomeLikeNum = itemView.findViewById(R.id.tvHomeLikeNum)
            tvHomeContent = itemView.findViewById(R.id.tvHomeContent)
            tvHomeTime = itemView.findViewById(R.id.tvHomeTime)
            btnHomeProfileMove = itemView.findViewById(R.id.btnHomeProfileMove)

            btnHomeProfileMove.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("uid", uid)
                context.startActivity(intent)
            }

            btnHomeProfileMove.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("uid", uid)
                context.startActivity(intent)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.home_board_template, null)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvHomeNick.text = boardHomeList[position].nick
        holder.tvHomeContent.text = boardHomeList[position].content
        holder.tvHomeTime.text = boardHomeList[position].time
        Glide.with(context).load(boardHomeList[position].image).into(holder.imgHomeContent)

        getHomeBoardImage(keyData[position], holder.imgHomeContent)
        getHomeBoardImage(boardHomeList[position].uid, holder.imgHomeProfile)

        holder.imgHomeComment.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("boardKey", keyData[position])
            intent.putExtra("profileUid", boardHomeList[position].uid)
            intent.putExtra("nick", boardHomeList[position].nick)
            intent.putExtra("content", boardHomeList[position].content)
            intent.putExtra("time", boardHomeList[position].time)

            context.startActivity(intent)
        }

        holder.btnHomeProfileMove.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("uid", boardHomeList[position].uid)

            context.startActivity(intent)
        }

        if (FBAuth.checkUid(boardHomeList[position].uid)) {
            holder.imgDialog.setOnClickListener {
                val array = arrayOf("수정", "삭제")
                var clickItem = ""
                Log.d("imgDialog", "click")
                AlertDialog.Builder(context)
                    .setTitle("게시글 관리")
                    .setItems(array) { dialog, which ->
                        clickItem = array[which]
                        Log.d("Dialog", "currentItem : $clickItem")
                        if (clickItem == "수정") {
                            Log.d("DialogEdit", "수정!!")
                            val intent = Intent(context, UpdateActivity::class.java)
                            intent.putExtra("boardKey", keyData[position])
                            context.startActivity(intent)
                        } else if (clickItem == "삭제") {
                            Log.d("DialogDelete", "삭제!!")
                            deleteBoard(boardHomeList[position], keyData[position]) // 삭제 호출
                        }
                    }
                    .show()

            }
        }
    }

    override fun getItemCount(): Int {
        return boardHomeList.size
    }

    fun getHomeBoardImage(key: String, view: ImageView) {
        val storageReference = Firebase.storage.reference.child("$key.png")

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("key", "Success")
                Glide.with(context)
                    .load(task.result)
                    .into(view)
            } else
                Log.d("key", "Fail")
        }
    }

}