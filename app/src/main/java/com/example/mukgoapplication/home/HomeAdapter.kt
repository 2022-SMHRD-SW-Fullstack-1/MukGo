package com.example.mukgoapplication.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mukgoapplication.R
import com.example.mukgoapplication.write.BoardVO
import org.w3c.dom.Text

class HomeAdapter(val context: Context, val boardHomeList: ArrayList<BoardVO>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHomeProfile: ImageView
        val tvHomeNick: TextView
        val imgHomeContent: ImageView
        val imgHomeLike: ImageView
        val imgHomeComment: ImageView
        val imgHomeBookmark: ImageView
        val tvHomeLikeNum: TextView
        val tvHomeContent: TextView
        val tvHomeTime: TextView

        init {
            imgHomeProfile = itemView.findViewById(R.id.imgHomeProfile)
            tvHomeNick = itemView.findViewById(R.id.tvHomeContent)
            imgHomeContent = itemView.findViewById(R.id.imgHomeLike)
            imgHomeLike = itemView.findViewById(R.id.imgHomeLike)
            imgHomeComment = itemView.findViewById(R.id.imgHomeComment)
            imgHomeBookmark = itemView.findViewById(R.id.imgHomeBookmark)
            tvHomeLikeNum = itemView.findViewById(R.id.tvHomeLikeNum)
            tvHomeContent = itemView.findViewById(R.id.tvHomeContent)
            tvHomeTime = itemView.findViewById(R.id.tvHomeTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.home_board_template, null)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHomeNick.text = boardHomeList[position].nick
//        holder.tvHomeLikeNum.text
        holder.tvHomeContent.text = boardHomeList[position].content
        holder.tvHomeTime.text = boardHomeList[position].time
    }

    override fun getItemCount(): Int {
        return boardHomeList.size
    }

}