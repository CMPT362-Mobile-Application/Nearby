package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseLocationViewModel: ViewModel() {
    val lat = MutableLiveData<Double>(0.0)
    val lng = MutableLiveData<Double>(0.0)
}