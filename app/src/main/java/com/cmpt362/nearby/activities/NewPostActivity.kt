package com.cmpt362.nearby.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.databinding.ActivityNewPostBinding
import com.cmpt362.nearby.viewmodels.NewPostViewModel
import java.text.SimpleDateFormat
import java.util.*

class NewPostActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityNewPostBinding
    private lateinit var categorySpinner: Spinner
    private lateinit var eventSwitch: SwitchCompat
    private lateinit var eventStartLayout: RelativeLayout
    private lateinit var eventEndLayout: RelativeLayout
    private lateinit var eventStartButton: Button
    private lateinit var eventEndButton: Button
    private lateinit var eventStartTextView: TextView
    private lateinit var eventEndTextView: TextView

    private lateinit var newPostViewModel: NewPostViewModel
    private val START_TEXT_KEY = "START TEXT KEY"
    private val END_TEXT_KEY = "END TEXT KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)

        // Setup view model for storing date/time entry and state
        newPostViewModel = ViewModelProvider(this).get(NewPostViewModel::class.java)

        // Set up Category Spinner
        categorySpinner = binding.addpostCategoryspinner
        ArrayAdapter.createFromResource(
            this,
            R.array.addpost_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        // Set up Is Event toggle
        eventSwitch = binding.addpostEvent
        eventStartLayout = binding.addpostEventstartlayout
        eventEndLayout = binding.addpostEventendlayout
        eventStartTextView = binding.addpostEventstarttext
        eventEndTextView = binding.addpostEventendtext
        eventSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                eventStartLayout.visibility = RelativeLayout.VISIBLE
                eventEndLayout.visibility = RelativeLayout.VISIBLE
            } else {
                eventStartLayout.visibility = RelativeLayout.GONE
                eventEndLayout.visibility = RelativeLayout.GONE
            }
        }

        // Event Set Start button
        eventStartButton = binding.addpostEventstartbutton
        eventStartButton.setOnClickListener {
            newPostViewModel.startOrEnd.value = "start"
            showDateTimePicker()
        }

        // Event set end button
        eventEndButton = binding.addpostEventendbutton
        eventEndButton.setOnClickListener {
            newPostViewModel.startOrEnd.value = "end"
            showDateTimePicker()
        }

        // Restore from saved instance state
        if (savedInstanceState?.getString(START_TEXT_KEY) != null)
            eventStartTextView.text = savedInstanceState.getString(START_TEXT_KEY)
        if (savedInstanceState?.getString(END_TEXT_KEY) != null)
            eventEndTextView.text = savedInstanceState.getString(END_TEXT_KEY)

        setContentView(binding.root)
    }

    fun showDateTimePicker() {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, this, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    // Will respond when the date has been entered and will open time picker dialog
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        // Store result in whichever button initiated the request
        if (newPostViewModel.startOrEnd.value == "start")
            newPostViewModel.startCalendar.value?.set(year, month, dayOfMonth)
        else if (newPostViewModel.startOrEnd.value == "end")
            newPostViewModel.endCalendar.value?.set(year, month, dayOfMonth)

        // Open TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, this,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
        timePickerDialog.show()
    }

    // Will respond once the time has been set, and update the text in the activity
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Store result in whichever button initiated the request
        if (newPostViewModel.startOrEnd.value == "start") {
            newPostViewModel.startCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newPostViewModel.startCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
            dateFormat.timeZone = newPostViewModel.startCalendar.value?.timeZone!!
            val dateTime = dateFormat.format(newPostViewModel.startCalendar.value?.time!!)
            eventStartTextView.text = dateTime
        }
        else if (newPostViewModel.startOrEnd.value == "end") {
            newPostViewModel.endCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newPostViewModel.endCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
            dateFormat.timeZone = newPostViewModel.endCalendar.value?.timeZone!!
            val dateTime = dateFormat.format(newPostViewModel.endCalendar.value?.time!!)
            eventEndTextView.text = dateTime
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(START_TEXT_KEY, eventStartTextView.text.toString())
        outState.putString(END_TEXT_KEY, eventEndTextView.text.toString())
    }
}