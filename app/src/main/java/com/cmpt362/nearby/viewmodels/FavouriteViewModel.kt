package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.activities.FavouriteActivity

class FavouriteViewModel: ViewModel() {
    val state = MutableLiveData<String>()

    init {
        state.value = FavouriteActivity.MYPOSTS_KEY // default to My Posts
    }
}