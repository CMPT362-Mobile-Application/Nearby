package com.cmpt362.nearby.classes

import android.icu.util.Calendar
import com.google.android.gms.common.internal.Objects
import com.google.firebase.firestore.GeoPoint
import com.cmpt362.nearby.classes.IconType
import com.cmpt362.nearby.classes.Color
import com.google.firebase.Timestamp

class Post(
    val userId : String = "",
    val title : String = "",
    val startTime : Timestamp = Timestamp(Calendar.getInstance().time),
    val endTime : Timestamp = Timestamp(Calendar.getInstance().time),
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val info: String = "",
    val tag: String = "",
    val imageUrl: String = "",
    val iconType: IconType = IconType.NONE,
    val iconColor: Color = Color.GREY,
    val isEvent: Boolean = false
)