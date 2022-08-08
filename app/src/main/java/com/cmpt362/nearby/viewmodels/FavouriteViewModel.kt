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

    fun loadFavouritePosts(favIds: MutableSet<String>, allPosts: ArrayList<Post>, allPostIds: ArrayList<String>) {
        if (favouritePosts.value != null) { // Check if posts already loaded
            favouritePosts.postValue(favouritePosts.value)
            return
        }

        CoroutineScope(IO).launch {
            val favPosts = ArrayList<Post>()
            val favPostIds = ArrayList<String>()
            for (favId in favIds) {
                val index = allPostIds.indexOf(favId)
                if (index > -1) {
                    favPosts.add(allPosts[index])
                    favPostIds.add(allPostIds[index])
                } else {
                    // would probably remove the link here since the post would likely be deleted
                }
            }
            if (favPosts.isNotEmpty()) {
                withContext(Main) {
                    favouritePostIds.value = favPostIds
                    favouritePosts.value = favPosts
                }
            }
        }
    }
    fun loadMyPosts(userId: String?, allPosts: ArrayList<Post>, allPostIds: ArrayList<String>) {
        if (myPosts.value != null) { // Check if posts already loaded
            myPosts.postValue(myPosts.value)
            return
        }

        CoroutineScope(IO).launch {
            val posts = ArrayList<Post>()
            val postIds = ArrayList<String>()
            allPosts.forEachIndexed { index, post ->
                if (post.userId == userId) {
                    posts.add(allPosts[index])
                    postIds.add(allPostIds[index])
                } else {
                    // would probably remove the link here since the post would likely be deleted
                }
            }
            if (posts.isNotEmpty()) {
                withContext(Main) {
                    myPostIds.value = postIds
                    myPosts.value = posts
                }
            }
        }
    }
}