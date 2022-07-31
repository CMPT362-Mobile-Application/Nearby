package com.cmpt362.nearby.classes

enum class IconType(val value: Int) {
    NONE(0), FOOD(1), GAME(2), SPORT(3);
    companion object {
        fun fromInt(value: Int) = IconType.values().first{ it.value == value }
    }
}

enum class Color(val value: Int) {
    GREY(0), RED(1), GREEN(2), BLUE(3);
    companion object {
        fun fromInt(value: Int) = IconType.values().first{ it.value == value }
    }
}