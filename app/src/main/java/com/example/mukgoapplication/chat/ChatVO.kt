package com.example.mukgoapplication.chat

data class ChatVO(
    var msg: String,
    var uid: String,    // =talker's uid
    var time: String,
)