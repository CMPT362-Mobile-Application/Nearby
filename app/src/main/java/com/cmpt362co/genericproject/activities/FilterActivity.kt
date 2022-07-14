package com.cmpt362co.genericproject.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362co.genericproject.databinding.ActivityFilterBinding
import java.text.SimpleDateFormat
import java.util.*

class FilterActivity : AppCompatActivity() {

    private var PREFERENCES_KEY = "cmpt362co"
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val earliestDate = Calendar.getInstance()
    private val latestDate = Calendar.getInstance()
    // Keys for the stored values
    private var EARLIEST_DATETIME_FILTER_KEY = "earliestDateTimeFilterKey"
    private var LATEST_DATETIME_FILTER_KEY = "latestDateTimeFilterKey"

    private lateinit var binding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)

        // Initialize the variables for Shared Preferences
        sharedPref = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        sharedPrefEditor = sharedPref.edit()

        // Get Values from shared preferences
        latestDate.time = Date(sharedPref.getLong(LATEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
        earliestDate.time = Date(sharedPref.getLong(EARLIEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))

        // General UI
        binding.filterLatestDateButton.text = SimpleDateFormat("MMM dd yyyy").format(
            latestDate.getTime()
        )
        binding.filterLatestTimeButton.text = SimpleDateFormat("hh:mm aa").format(
            latestDate.getTime()
        )
        binding.filterEarliestDateButton.text = SimpleDateFormat("MMM dd yyyy").format(
            earliestDate.getTime()
        )
        binding.filterEarliestTimeButton.text = SimpleDateFormat("hh:mm aa").format(
            earliestDate.getTime()
        )



        // Listeners
        binding.filterEarliestDateButton.setOnClickListener {
            val dialog = DatePickerDialog(this)
            dialog.updateDate(earliestDate.get(Calendar.YEAR), earliestDate.get(Calendar.MONTH), earliestDate.get(Calendar.DAY_OF_MONTH))
            dialog.setOnDateSetListener { dp, y, m, d ->
                earliestDate.set(Calendar.YEAR, y)
                earliestDate.set(Calendar.MONTH, m)
                earliestDate.set(Calendar.DAY_OF_MONTH, d)
                sharedPrefEditor.putLong(EARLIEST_DATETIME_FILTER_KEY, earliestDate.timeInMillis)
                sharedPrefEditor.commit()
                earliestDate.time = Date(sharedPref.getLong(EARLIEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
                binding.filterEarliestDateButton.text = SimpleDateFormat("MMM dd yyyy").format(
                    earliestDate.getTime()
                )
            }
            dialog.show()
        }
        binding.filterEarliestTimeButton.setOnClickListener {
            val tp = TimePickerDialog.OnTimeSetListener { dp, h, m ->
                earliestDate.set(Calendar.HOUR_OF_DAY, h)
                earliestDate.set(Calendar.MINUTE, m)
                sharedPrefEditor.putLong(EARLIEST_DATETIME_FILTER_KEY, earliestDate.timeInMillis)
                sharedPrefEditor.commit()
                earliestDate.time = Date(sharedPref.getLong(EARLIEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
                binding.filterEarliestTimeButton.text = SimpleDateFormat("hh:mm aa").format(
                    earliestDate.getTime()
                )
            }
            val dialog = TimePickerDialog(this, tp, earliestDate.get(Calendar.HOUR_OF_DAY), earliestDate.get(Calendar.MINUTE), false)
            dialog.show()
        }
        binding.filterLatestDateButton.setOnClickListener {
            val dialog = DatePickerDialog(this)
            dialog.updateDate(latestDate.get(Calendar.YEAR), latestDate.get(Calendar.MONTH), latestDate.get(Calendar.DAY_OF_MONTH))
            dialog.setOnDateSetListener { dp, y, m, d ->
                latestDate.set(Calendar.YEAR, y)
                latestDate.set(Calendar.MONTH, m)
                latestDate.set(Calendar.DAY_OF_MONTH, d)
                sharedPrefEditor.putLong(LATEST_DATETIME_FILTER_KEY, latestDate.timeInMillis)
                sharedPrefEditor.commit()
                latestDate.time = Date(sharedPref.getLong(LATEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
                binding.filterLatestDateButton.text = SimpleDateFormat("MMM dd yyyy").format(
                    latestDate.getTime()
                )
            }
            dialog.show()
        }
        binding.filterLatestTimeButton.setOnClickListener {
            val tp = TimePickerDialog.OnTimeSetListener { dp, h, m ->
                latestDate.set(Calendar.HOUR_OF_DAY, h)
                latestDate.set(Calendar.MINUTE, m)
                sharedPrefEditor.putLong(LATEST_DATETIME_FILTER_KEY, latestDate.timeInMillis)
                sharedPrefEditor.commit()
                latestDate.time = Date(sharedPref.getLong(LATEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
                binding.filterLatestTimeButton.text = SimpleDateFormat("hh:mm aa").format(
                    latestDate.getTime()
                )
            }
            val dialog = TimePickerDialog(this, tp, latestDate.get(Calendar.HOUR_OF_DAY), latestDate.get(Calendar.MINUTE), false)
            dialog.show()
        }

        setContentView(binding.root)
    }
}