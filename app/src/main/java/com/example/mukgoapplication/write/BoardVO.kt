package com.example.mukgoapplication.write

data class BoardVO(
    var content: String = "",
    var uid: String = "",
    var time: String = "",
    val nick: String = "",
    val like: String = "0",
)