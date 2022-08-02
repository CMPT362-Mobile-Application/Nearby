package com.cmpt362.nearby.classes

import com.google.android.gms.common.internal.Objects
import com.google.firebase.firestore.GeoPoint
import com.cmpt362.nearby.classes.IconType
import com.cmpt362.nearby.classes.Color

class Post(
    val userId : String = "",
    val title : String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val info: String = "",
    val tag: String = "",
    val imageUrl: String = "",
    val iconType: IconType = IconType.NONE,
    val iconColor: Color = Color.GREY,
    val isEvent: Boolean = false,
    val favouritesCounter: Int = 0
)