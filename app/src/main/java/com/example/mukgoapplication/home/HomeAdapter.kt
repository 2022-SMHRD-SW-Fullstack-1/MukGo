package com.example.mukgoapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.reflect.Member

class HomeAdapter(
    val context: Context,
    val boardHomeList: ArrayList<BoardVO>,
    var keyData: ArrayList<String>,
    var bookmarkList: ArrayList<String>,
    var likeList: ArrayList<String>

) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    val uid = FBAuth.getUid()
    val database = Firebase.database
    val auth: FirebaseAuth = Firebase.auth

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
        val imgDialog: ImageView

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

            imgDialog = itemView.findViewById(R.id.imgDialog)

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

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvHomeNick.text = boardHomeList[position].nick
        holder.tvHomeContent.text = boardHomeList[position].content
        holder.tvHomeTime.text = boardHomeList[position].time
        holder.tvHomeLikeNum.text = boardHomeList[position].like

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
        } else {
            holder.imgDialog.visibility = View.INVISIBLE
        }

//        좋아요 클릭

        holder.imgHomeLike.setOnClickListener {
            val likeRef = database.getReference("like")
            val fragment1 = Fragment1_home()
            val bundle = Bundle()
            bundle.putString("boardKey", keyData[position])

            fragment1.arguments = bundle

            if (likeList.contains(keyData[position])) {
//                북마크 취소 database에 해당 bookData 삭제
                likeRef.child(uid).child(keyData[position]).removeValue()
                holder.imgHomeLike.setImageResource(R.drawable.heartblank)
                holder.tvHomeLikeNum.text = holder.tvHomeLikeNum.text

            } else {
                likeRef.child(uid).child(keyData[position]).setValue("like")
                holder.imgHomeLike.setImageResource(R.drawable.heartfull)
//                북마크 추가 database에 해당 bookData 추가
            }
        }

//        좋아요 표시

        if (likeList.contains(keyData[position])) {
            holder.imgHomeLike.setImageResource(R.drawable.heartfull)
        } else {
            holder.imgHomeLike.setImageResource(R.drawable.heartblank)
        }


//        북마크 표시
        if (bookmarkList.contains(keyData[position])) {
            holder.imgHomeBookmark.setImageResource(R.drawable.bookmarkfull)
        } else {
            holder.imgHomeBookmark.setImageResource(R.drawable.bookmarkblank)
        }

//        북마크 클릭 시

        holder.imgHomeBookmark.setOnClickListener {
            val bookmarkRef = database.getReference("bookmarkList")
            Log.d("data1BookmarkList", bookmarkList.toString())
            if (bookmarkList.contains(keyData[position])) {
//                북마크 취소 database에 해당 bookData 삭제
                bookmarkRef.child(uid).child(keyData[position]).removeValue()
                holder.imgHomeBookmark.setImageResource(R.drawable.bookmarkblank)
                Log.d("data1", "취소 success")

            } else {
                bookmarkRef.child(auth.currentUser!!.uid).child(keyData[position]).setValue("good")
                holder.imgHomeBookmark.setImageResource(R.drawable.bookmarkfull)
//                북마크 추가 database에 해당 bookData 추가
                Log.d("data1List", bookmarkList.toString())
                Log.d("data1", keyData[position].toString())
            }
            Log.d("data1List", bookmarkList.toString())
        }


    }

    override fun getItemCount(): Int {
        return boardHomeList.size
    }

    fun getHomeLikeNum(key: String, et: EditText) {
        FBDatabase.getLikeRef().get().addOnSuccessListener {
            val item = it.getValue() as String
        }
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

    /**게시글 삭제*/
    fun deleteBoard(board: BoardVO, key: String) {
        Log.d("DialogDeleteFun", "삭제함수")
        Log.d("DialogDeleteFunBoard", board.toString())
        Log.d("DialogDeleteFunBoardKey", key)
        val data = FBDatabase.getAllBoardRef().child(key)
        Log.d("DialogDeleteFunBoardData", data.toString())
        data.removeValue()
    }

    fun getBoardData(uid: String) {

    }
}
