package com.cmpt362.nearby.classes

import com.google.firebase.firestore.GeoPoint


data class Post(
    val userId : String,
    val title : String,
    val location: GeoPoint,
    val info: String,
    val comments: ArrayList<Comment> = arrayListOf(),
    val imageUrl: String = "",
    val iconType: IconType = IconType.NONE,
    val iconColor: Color = Color.GREY
)