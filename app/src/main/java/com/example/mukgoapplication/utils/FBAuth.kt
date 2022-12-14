package com.example.mukgoapplication.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class FBAuth {

    companion object{
        lateinit var auth: FirebaseAuth

        /**uid를 가져온다*/
        fun getUid():String{
            auth = FirebaseAuth.getInstance() // class 안에서 인스턴스 생성할 때 getInstance()붙여준다
            return auth.currentUser!!.uid
        }
        
        /**현재시간을 가져온다*/
        fun getTime(): String{
            // Calendar 객체는 getInstance() 메소드로 객체를 생성한다
            val currentTime = Calendar.getInstance().time
            // 시간을 나타낼 형식, 어느위치의 시간을 가져올건지 설정
            // "yyyy.MM.dd HH:mm:sss"
            val time = SimpleDateFormat("yyyy.MM.dd HH:mm:sss", Locale.KOREAN).format(currentTime)
            return time
        }

        /**사용자uid와 targetUid 비교
         * @return Boolean*/
        fun checkUid(targetUid: String):Boolean {
            val userUid = FBAuth.getUid()
            Log.d("check"," 사용자uid: $userUid, targetUid: $targetUid")
            return targetUid==userUid
        }

    }
}