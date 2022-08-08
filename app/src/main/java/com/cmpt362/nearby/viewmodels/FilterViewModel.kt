package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class FilterViewModel: ViewModel() {
    val selectedTags = MutableLiveData<ArrayList<String>>()
    val earliestCalendar = MutableLiveData<Calendar>()
    val latestCalendar = MutableLiveData<Calendar>()
    val timePickerSelected = MutableLiveData<String>()
}