package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.database.DbFilter
import com.cmpt362.nearby.database.FirestoreDatabase

class PostsViewModel: ViewModel() {
    private val _postList: MutableLiveData<ArrayList<Post>> =
        MutableLiveData(arrayListOf())
    val postsList: LiveData<ArrayList<Post>> get() { return _postList }

    private val _idList: MutableLiveData<ArrayList<String>> =
        MutableLiveData(arrayListOf())
    val idList: LiveData<ArrayList<String>> get() { return _idList }

    private var _filter: DbFilter = DbFilter.Builder().build()

    init {
        updatePosts()
    }

    private fun updatePosts() {
        FirestoreDatabase.registerPostsListener(_filter) { posts, ids ->
            _postList.value = posts
            _idList.value = ids
        }
    }

    fun addPost(post: Post) {
        FirestoreDatabase.addPost(post)
    }
}