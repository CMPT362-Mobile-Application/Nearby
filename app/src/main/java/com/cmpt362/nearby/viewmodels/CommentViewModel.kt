package com.cmpt362.nearby.viewmodels

import android.util.MutableInt
import android.util.MutableLong
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.nearby.classes.Comment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentViewModel: ViewModel() {
    val replyingTo = MutableLiveData<Long>()
    val commentList: MutableLiveData<ArrayList<Comment>> =
        MutableLiveData(arrayListOf())
    // should be controlled by database
    private var latestCommentId : Long = 1355

    init {
        replyingTo.value = -1

        // This Portion should be removed when the data repository is added
        commentList.value = arrayListOf(
            Comment(99, "This is awesome! Can't Wait", "Time posted"),
            Comment(102, "I will destroy all of you noobs", "Time posted"),
            Comment(134, "No u", "Time posted", 102),
            Comment(145, "You are going down first >.<", "Time posted", 102),
            Comment(1111, "Same! I haven't played in a tournament for so long..",
                "Time posted", 99),
            Comment(1355, "Wario is the best", "Time posted", 102)
        )

    }

    fun addComment(commentTxt: String) {
        // TODO: convert to local time string
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            .format(Calendar.getInstance().time)

        val comment = Comment(++latestCommentId, commentTxt, time)
        commentList.value?.add(comment)
        // let the observer know that the list has changed
        commentList.value = commentList.value
    }
}