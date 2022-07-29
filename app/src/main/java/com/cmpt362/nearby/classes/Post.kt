package com.cmpt362.nearby.classes

import com.google.android.gms.common.internal.Objects
import com.google.firebase.firestore.GeoPoint


enum class IconType(val value: Int) {
    NONE(0), FOOD(1), GAME(2), SPORT(3)
}

enum class Color(val value: Int) {
    GREY(0), RED(1), GREEN(2), BLUE(3)
}

class Post(
    val userId : String,
    val title : String,
    val location: GeoPoint,
    val info: String,
    val comments: ArrayList<Comment> = arrayListOf(),
    val imageUrl: String = "",
    val iconType: IconType = IconType.NONE,
    val iconColor: Color = Color.GREY
)