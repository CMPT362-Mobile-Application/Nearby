package com.cmpt362co.genericproject.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel: ViewModel() {
    val selectedTags = MutableLiveData<ArrayList<String>>()
}