package com.cmpt362.nearby.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class FilterViewModel: ViewModel() {
    val selectedTags = MutableLiveData<ArrayList<String>>()
    val startCalendar = MutableLiveData<Calendar>()
    val endCalendar = MutableLiveData<Calendar>()
}