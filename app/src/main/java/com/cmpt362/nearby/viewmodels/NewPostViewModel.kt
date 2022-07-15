package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class NewPostViewModel: ViewModel() {
    val startCalendar = MutableLiveData<Calendar>()
    val endCalendar = MutableLiveData<Calendar>()
    val startOrEnd = MutableLiveData<String>()

    init {
        startCalendar.value = Calendar.getInstance()
        endCalendar.value = Calendar.getInstance()
    }
}