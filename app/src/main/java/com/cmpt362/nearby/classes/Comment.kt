package com.cmpt362.nearby.classes
import com.google.firebase.Timestamp
import java.util.*

private val NO_REFERENCE = -1L

// TODO: make comment use epoch timestamp as it is the method of storage for firebase
class Comment(
    val id: Long = -1,
    val info: String = "",
    val timeStamp: Timestamp = Timestamp(Calendar.getInstance().time),
    val refId: Long = NO_REFERENCE) {
    companion object {
        val NO_REF = NO_REFERENCE
    }
}