package com.cmpt362.nearby.classes

import com.google.android.gms.common.internal.Objects
import com.google.firebase.firestore.GeoPoint

enum class IconType(val value: Int) {
    NONE(0), FOOD(1), GAME(2), SPORT(3);
    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}

enum class Color(val value: Int) {
    GREY(0), RED(1), GREEN(2), BLUE(3);
    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}

class Post(
    val userId : String = "",
    val title : String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val info: String = "",
    val tag: String = "",
    val imageUrl: String = "",
    val iconType: IconType = IconType.NONE,
    val iconColor: Color = Color.GREY,
    val isEvent: Boolean = false
)