package com.cmpt362.nearby.viewmodels

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.activities.FavouriteActivity
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.database.FirestoreDatabase
import com.cmpt362.nearby.database.PostFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel(): ViewModel() {
    val state = MutableLiveData<String>()
    val favouritePosts = MutableLiveData<ArrayList<Post>>()
    val favouritePostIds = MutableLiveData<ArrayList<String>>()
    val myPosts = MutableLiveData<ArrayList<Post>>()
    val myPostIds = MutableLiveData<ArrayList<String>>()

    init {
        state.value = FavouriteActivity.MYPOSTS_KEY // default to My Posts
    }

    fun loadFavouritePosts(favIds: MutableSet<String>, allIdPostPairs: ArrayList<Pair<String, Post>>) {
        if (favouritePosts.value != null) { // Check if posts already loaded
            favouritePosts.postValue(favouritePosts.value)
            return
        }

        CoroutineScope(IO).launch {
            val filteredPairs = PostFilter.Builder().includeIds(favIds).build()
                .filter(allIdPostPairs)
            if (filteredPairs.isNotEmpty()) {
                withContext(Main) {
                    favouritePostIds.value = filteredPairs.map { it.first } as ArrayList
                    favouritePosts.value = filteredPairs.map { it.second } as ArrayList
                }
            }
        }
    }
    fun loadMyPosts(myIds: MutableSet<String>, allIdPostPairs: ArrayList<Pair<String, Post>>) {
        if (myPosts.value != null) { // Check if posts already loaded
            myPosts.postValue(myPosts.value)
            return
        }

        CoroutineScope(IO).launch {
            val filteredPairs = PostFilter.Builder().includeIds(myIds).build()
                .filter(allIdPostPairs)
            if (filteredPairs.isNotEmpty()) {
                withContext(Main) {
                    myPostIds.value = filteredPairs.map { it.first } as ArrayList
                    myPosts.value = filteredPairs.map { it.second } as ArrayList
                }
            }
        }
    }
}