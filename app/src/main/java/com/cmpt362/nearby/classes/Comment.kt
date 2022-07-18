package com.cmpt362.nearby.classes

// TODO: make comment use epoch timestamp as it is the method of storage for firebase
class Comment(val commentId: Long,
              val commentInfo: String,
              val dtime: String,
              val replyTo: Long = -1) {
    val id: Long
    val info: String
    val time: String
    val replyId: Long

    init {
        id = commentId
        info = commentInfo
        time = dtime
        replyId = replyTo
    }
}