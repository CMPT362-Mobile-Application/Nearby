package com.cmpt362.nearby.viewmodels

import androidx.lifecycle.*
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.database.FirestoreDatabase

class CommentViewModel(private val postId: String): ViewModel() {
    val replyingTo: MutableLiveData<Long> = MutableLiveData(Comment.NO_REF)

    private val _commentList =
        FirestoreDatabase.getComments(postId).asLiveData(viewModelScope.coroutineContext)
    val commentList: LiveData<ArrayList<Comment>> get() { return _commentList }

    fun addComment(commentTxt: String) {
        FirestoreDatabase.addComment(Comment(0, commentTxt, replyingTo.value!!), postId)
    }
    
}