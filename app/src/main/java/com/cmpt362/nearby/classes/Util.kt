package com.cmpt362.nearby.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.viewmodels.CommentViewModel
import com.cmpt362.nearby.viewmodels.FavouriteViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun makeUuid(): String {
        return UUID.randomUUID().toString().replace("-", "").uppercase()
    }

    fun timestampToDateStr(timestamp: Timestamp): String {
        val SEC_IN_MIN = 60
        val secondsDiff =
            Timestamp(Calendar.getInstance().time).seconds -timestamp.seconds

        return if (secondsDiff < 40) {
            "a few seconds ago"
        } else if (secondsDiff < SEC_IN_MIN * 15) {
            "${secondsDiff/SEC_IN_MIN} minutes ago"
        } else {
            val df = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
            df.format(timestamp.toDate())
        }
    }

    // https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
    // custom ViewModel factory to pass in additional postId parameter when creating
    // CommentViewModel
    class CommentViewModelFactory(private val postId: String):
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CommentViewModel(postId) as T
    }

}