package com.cmpt362.nearby.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.cmpt362.nearby.R
import com.cmpt362.nearby.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityNewPostBinding
    private lateinit var categorySpinner: Spinner
    private lateinit var eventSwitch: SwitchCompat
    private lateinit var eventStartLayout: LinearLayout
    private lateinit var eventEndLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)

        categorySpinner = binding.addpostCategoryspinner
        ArrayAdapter.createFromResource(
            this,
            R.array.addpost_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        eventSwitch = binding.addpostEvent
        eventStartLayout = binding.addpostEventstartlayout
        eventEndLayout = binding.addpostEventendlayout
        eventSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                eventStartLayout.visibility = LinearLayout.VISIBLE
                eventEndLayout.visibility = LinearLayout.VISIBLE
            } else {
                eventStartLayout.visibility = LinearLayout.GONE
                eventEndLayout.visibility = LinearLayout.GONE
            }
        }

        setContentView(binding.root)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

    }
}