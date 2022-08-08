package com.cmpt362.nearby.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.classes.Color
import com.cmpt362.nearby.classes.IconType
import java.util.*

class NewPostViewModel: ViewModel() {
    val startCalendar = MutableLiveData<Calendar>()
    val endCalendar = MutableLiveData<Calendar>()
    val startOrEnd = MutableLiveData<String>()
    val imageBitmap = MutableLiveData<Bitmap>()
    val icon = MutableLiveData<IconType>()
    val color = MutableLiveData<Color>()
    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)

    init {
        startCalendar.value = Calendar.getInstance()
        endCalendar.value = Calendar.getInstance()
    }
}