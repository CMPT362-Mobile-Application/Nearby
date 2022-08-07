package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.*
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.database.DbFilter
import com.cmpt362.nearby.database.FirestoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class PostsViewModel: ViewModel() {
    private val _filter: MutableLiveData<DbFilter> =
        MutableLiveData(DbFilter.Builder().build())

    private val _idPostPairs = FirestoreDatabase.getPosts()
        .distinctUntilChanged().asLiveData(viewModelScope.coroutineContext)
    val idPostPairs: LiveData<ArrayList<Pair<String, Post>>> get() { return _idPostPairs }

    fun addPost(post: Post): String {
        return FirestoreDatabase.addPost(post)
    }
}