package com.cmpt362.nearby.viewmodels

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.activities.FavouriteActivity
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.database.FirestoreDatabase

class FavouriteViewModel(): ViewModel() {
    val state = MutableLiveData<String>()

    init {
        state.value = FavouriteActivity.MYPOSTS_KEY // default to My Posts
    }

}