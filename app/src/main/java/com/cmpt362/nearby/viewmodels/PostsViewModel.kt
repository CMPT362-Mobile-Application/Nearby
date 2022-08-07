package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.*
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.database.DbFilter
import com.cmpt362.nearby.database.FirestoreDatabase
import kotlinx.coroutines.launch

class PostsViewModel: ViewModel() {
    private val _postList = MutableLiveData<ArrayList<Post>>()
    val postList: LiveData<ArrayList<Post>> get() { return _postList }

    private val _idList = MutableLiveData<ArrayList<String>>()
    val idList: LiveData<ArrayList<String>> get() { return _idList }

    private val _filter: MutableLiveData<DbFilter> =
        MutableLiveData(DbFilter.Builder().build())

    init {
        viewModelScope.launch {
            FirestoreDatabase.getPosts().collect { postIdPair ->
                _postList.value = postIdPair.first ?: arrayListOf()
                _idList.value = postIdPair.second ?: arrayListOf()
            }
        }
    }

    fun addPost(post: Post) {
        FirestoreDatabase.addPost(post)
    }
}