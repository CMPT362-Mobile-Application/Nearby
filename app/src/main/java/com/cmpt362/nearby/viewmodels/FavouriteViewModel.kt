package com.cmpt362.nearby.viewmodels

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.activities.FavouriteActivity
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.database.FirestoreDatabase

class FavouriteViewModel(private val postId: String): ViewModel() {
    val state = MutableLiveData<String>()
    val _favouritesList: MutableLiveData<ArrayList<String>> =
        MutableLiveData(arrayListOf())
    val favouritesList: LiveData<ArrayList<String>> get() { return _favouritesList }

    init {
        state.value = FavouriteActivity.MYPOSTS_KEY // default to My Posts
        updateFavourites()
    }

    private fun updateFavourites() {
        FirestoreDatabase.registerFavouritesListener(postId) { favourite ->
            Log.i("favouritesListener", "updateFavourites: " + favourite)
            _favouritesList.value?.add(favourite)
            Log.i("favouritesListener", "updateFavourites: " + _favouritesList.value)
        }
    }

    fun clickFavourite(postId: String, activity: FragmentActivity?) {
        FirestoreDatabase.incrementFavouritePost(postId, activity)
    }
}