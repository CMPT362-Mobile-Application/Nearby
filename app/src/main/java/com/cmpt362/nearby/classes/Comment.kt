package com.cmpt362.nearby.classes
import com.google.firebase.Timestamp
import java.util.*

// TODO: make comment use epoch timestamp as it is the method of storage for firebase
data class Comment(
    val id: Long = 0L,
    val info: String = "",
    val replyId: Long = -1L,
    val timestamp: Timestamp = Timestamp(Calendar.getInstance().time)) {

    companion object {
        const val NO_REF = -1L
    }
}