package com.cmpt362.nearby.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.nearby.R
import com.cmpt362.nearby.adapters.FilterListViewAdapter
import com.cmpt362.nearby.classes.Util
import com.cmpt362.nearby.databinding.ActivityFilterBinding
import com.cmpt362.nearby.viewmodels.FilterViewModel
import java.util.*
import kotlin.collections.ArrayList

class FilterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var tagsSharedPref: SharedPreferences
    private val filterViewModel: FilterViewModel by viewModels()
    private lateinit var filterListViewAdapter: FilterListViewAdapter

    companion object {
        // Keys for the stored values
        const val PREFERENCES_KEY = "filter"
        const val EARLIEST_DATETIME_FILTER_KEY = "earliestDateTimeFilterKey"
        const val LATEST_DATETIME_FILTER_KEY = "latestDateTimeFilterKey"
        const val TAGS_PREFERENCES_KEY = "filterTags"
    }

    private lateinit var binding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        // Get the viewmodel
        if (filterViewModel.selectedTags.value == null) {
            filterViewModel.selectedTags.value = ArrayList()}

        // Init viewadapter
        filterListViewAdapter = FilterListViewAdapter(this, filterViewModel.selectedTags.value!!)
        filterViewModel.selectedTags.observe(this) {
            filterListViewAdapter.notifyDataSetChanged()
        }

        // Initialize the variables for Shared Preferences
        sharedPref = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        tagsSharedPref = getSharedPreferences(TAGS_PREFERENCES_KEY, Context.MODE_PRIVATE)

        setupSelectedTime()
        setupTagSelection()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    fun onTimeBtnClick(view: View) {
        when (view.id) {
            R.id.filter_earliest_btn -> {
                filterViewModel.timePickerSelected.value = "earliest"
            }
            R.id.filter_latest_btn -> {
                filterViewModel.timePickerSelected.value = "latest"
            }
        }

        Util.showDatePicker(this, this)
    }

    fun onClickExit(view: View) {
        when (view.id) {
            R.id.filter_apply_button -> {
                // store the earliest and latest time of post in shared preferences
                with (sharedPref.edit()) {
                    filterViewModel.earliestCalendar.value?.let {
                        putLong(EARLIEST_DATETIME_FILTER_KEY, it.timeInMillis)
                    }
                    filterViewModel.latestCalendar.value?.let {
                        putLong(LATEST_DATETIME_FILTER_KEY, it.timeInMillis)
                    }
                    apply()
                }

                // store all tags in shared preferences
                with (tagsSharedPref.edit()) {
                    clear()
                    filterViewModel.selectedTags.value?.forEach {
                        putString(it, it)
                    }
                    apply()
                }
                finish()
            }

            R.id.filter_clear_button -> {
                with (sharedPref.edit()) {
                    clear()
                    apply()
                    binding.filterEarliestTv.text = getString(R.string.no_earliest_date_time_set)
                    binding.filterLatestTv.text = getString(R.string.no_latest_date_time_set)
                }
                with (tagsSharedPref.edit()) {
                    clear()
                    apply()
                    filterViewModel.selectedTags.value?.clear()
                    filterListViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setupSelectedTime() {
        val cal = Calendar.getInstance()
        val earliestMillis = sharedPref.getLong(EARLIEST_DATETIME_FILTER_KEY, -1L)
        val latestMillis = sharedPref.getLong(LATEST_DATETIME_FILTER_KEY, -1L)

        if (earliestMillis != -1L) {
            cal.time = Date(earliestMillis)
            binding.filterEarliestTv.text = Util.calendarToStr(cal)
        }

        if (latestMillis != -1L) {
            cal.time = Date(latestMillis)
            binding.filterLatestTv.text = Util.calendarToStr(cal)
        }
    }

    private fun setupTagSelection() {
        //Adapters
        binding.filterSelectedTagsList.adapter = filterListViewAdapter

        // initialize previously set tags
        filterViewModel.selectedTags.value?.addAll(tagsSharedPref.all.keys)

        // Get Values from shared preferences
        // Set up Category Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.addpost_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.filterTagsSelector.adapter = adapter
        }

        binding.filterTagsSelectorButton.setOnClickListener {
            val tag = binding.filterTagsSelector.selectedItem as String
            if (filterViewModel.selectedTags.value != null && tag.trim().isNotEmpty())
            {
                if (!filterViewModel.selectedTags.value!!.contains(tag))
                {
                    filterViewModel.selectedTags.value!!.add(tag)
                    filterListViewAdapter.notifyDataSetChanged()
                }
            }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (filterViewModel.timePickerSelected.value == "earliest")
            filterViewModel.earliestCalendar.value?.set(year, month, dayOfMonth)
        else if (filterViewModel.timePickerSelected.value == "latest")
            filterViewModel.latestCalendar.value?.set(year, month, dayOfMonth)

        Util.showTimePicker(this, this)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Store result in whichever button initiated the request
        if (filterViewModel.timePickerSelected.value == "earliest") {
            filterViewModel.earliestCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            filterViewModel.earliestCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            binding.filterEarliestTv.text =
                Util.calendarToStr(filterViewModel.earliestCalendar.value!!)
        }
        else if (filterViewModel.timePickerSelected.value == "latest") {
            filterViewModel.latestCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            filterViewModel.latestCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            binding.filterLatestTv.text =
                Util.calendarToStr(filterViewModel.latestCalendar.value!!)
        }
    }
}