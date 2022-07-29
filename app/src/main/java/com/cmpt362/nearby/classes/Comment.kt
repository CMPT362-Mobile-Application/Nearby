package com.cmpt362.nearby.classes
import com.google.firebase.Timestamp
import java.util.*

// TODO: make comment use epoch timestamp as it is the method of storage for firebase
data class Comment(val id: String, val info: String, val refId: String? = null) {
    val timeStamp: Timestamp = Timestamp(Calendar.getInstance().time)
}