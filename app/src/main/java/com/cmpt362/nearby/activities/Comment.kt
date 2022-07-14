package com.cmpt362.nearby.activities

class Comment(val commentId: Long,
              val commentInfo: String,
              val dtime: String,
              val replyId: Long = -1) {
    val id: Long
    val info: String
    val time: String
    val replyTo: Long

    init {
        id = commentId
        info = commentInfo
        time = dtime
        replyTo = replyId
    }
}