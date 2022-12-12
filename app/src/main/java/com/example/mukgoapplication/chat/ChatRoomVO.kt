package com.example.mukgoapplication.chat

data class ChatRoomVO(
    var chatroomKey: String,
    var opponentUID: String,
    var lastChatMsg: String,
    var lastChatTime: String,
)