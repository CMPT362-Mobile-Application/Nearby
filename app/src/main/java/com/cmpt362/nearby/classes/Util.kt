package com.cmpt362.nearby.classes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.viewmodels.CommentViewModel
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

    fun timestampToDateStrPost(timestamp: Timestamp): String {
        val df = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
        return df.format(timestamp.toDate())
    }

    fun calendarToStr(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
        dateFormat.timeZone = calendar.timeZone
        return dateFormat.format(calendar.time)
    }

    fun millisToTimeStamp(ms: Long): Timestamp? {
        return if (ms == -1L) { null }
            else { Timestamp(Date(ms)) }
    }

    fun showDatePicker(context: Context, listener: DatePickerDialog.OnDateSetListener) {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(context, listener, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    fun showTimePicker(context: Context, listener: TimePickerDialog.OnTimeSetListener) {
        val cal = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(context, listener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
        timePickerDialog.show()
    }


    fun hasNetworkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null &&
                cm.getNetworkCapabilities(cm.activeNetwork) != null
    }


    // https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
    // custom ViewModel factory to pass in additional postId parameter when creating
    // CommentViewModel
    class CommentViewModelFactory(private val postId: String):
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CommentViewModel(postId) as T
    }

}