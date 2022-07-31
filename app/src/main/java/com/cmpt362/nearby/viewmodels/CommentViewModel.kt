package com.cmpt362.nearby.viewmodels

import android.util.MutableInt
import android.util.MutableLong
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.database.FirestoreDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentViewModel(private val postId: String): ViewModel() {
    val replyingTo: MutableLiveData<Long> = MutableLiveData(Comment.NO_REF)

    private val _commentList: MutableLiveData<ArrayList<Comment>> =
        MutableLiveData(arrayListOf())
    val commentList: LiveData<ArrayList<Comment>> get() { return _commentList }

    init {
        getComments()
    }

    private fun getComments() {
        FirestoreDatabase().getComments(postId) { comments ->
            _commentList.value = comments }
    }

    fun addComment(commentTxt: String) {
        replyingTo.value?.let {
            FirestoreDatabase().addComment(Comment(0, commentTxt, it), postId)
        }
        // retrieve the latest changes from the database
        getComments()
    }
}