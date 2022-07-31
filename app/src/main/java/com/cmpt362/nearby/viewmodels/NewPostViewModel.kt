package com.cmpt362.nearby.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class NewPostViewModel: ViewModel() {
    val startCalendar = MutableLiveData<Calendar>()
    val endCalendar = MutableLiveData<Calendar>()
    val startOrEnd = MutableLiveData<String>()
    val imageBitmap = MutableLiveData<Bitmap>()
    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)

    init {
        val cal = Calendar.getInstance()
        startCalendar.value = cal
        endCalendar.value = cal
    }
}