package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.*
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.database.PostFilter
import com.cmpt362.nearby.database.FirestoreDatabase
import kotlinx.coroutines.flow.distinctUntilChanged

class PostsViewModel: ViewModel() {
    private val _idPostPairs = FirestoreDatabase.getPosts()
        .distinctUntilChanged().asLiveData(viewModelScope.coroutineContext)
    val idPostPairs: LiveData<ArrayList<Pair<String, Post>>> get() { return _idPostPairs }
}