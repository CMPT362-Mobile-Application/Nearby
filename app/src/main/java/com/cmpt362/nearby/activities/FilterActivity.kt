package com.cmpt362.nearby.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.adapters.FilterListViewAdapter
import com.cmpt362.nearby.databinding.ActivityFilterBinding
import com.cmpt362.nearby.viewmodels.FilterViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FilterActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    private lateinit var filterViewModel: FilterViewModel

    private lateinit var filterListViewAdapter: FilterListViewAdapter

    private val earliestDate = Calendar.getInstance()
    private val latestDate = Calendar.getInstance()
    // Keys for the stored values
    companion object {
        val PREFERENCES_KEY = "cmpt362"
        val EARLIEST_DATETIME_FILTER_KEY = "earliestDateTimeFilterKey"
        val LATEST_DATETIME_FILTER_KEY = "latestDateTimeFilterKey"
    }

    private lateinit var binding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        // Get the viewmodel
        filterViewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        if (filterViewModel.selectedTags.value == null) {filterViewModel.selectedTags.value = ArrayList()}

        // Init viewadapter
        filterListViewAdapter = FilterListViewAdapter(this, filterViewModel.selectedTags.value!!)
        filterViewModel.selectedTags.observe(this) {
            filterListViewAdapter.notifyDataSetChanged()
        }

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
        //Adapters
        binding.filterSelectedTagsList.adapter = filterListViewAdapter

        // Listeners
        binding.filterClearButton.setOnClickListener {
            filterViewModel.selectedTags.value!!.clear()
            filterListViewAdapter.notifyDataSetChanged()
            sharedPrefEditor.remove(LATEST_DATETIME_FILTER_KEY)
            sharedPrefEditor.remove(EARLIEST_DATETIME_FILTER_KEY)
            sharedPrefEditor.commit()
            latestDate.time = Date(sharedPref.getLong(LATEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
            earliestDate.time = Date(sharedPref.getLong(EARLIEST_DATETIME_FILTER_KEY, Calendar.getInstance().timeInMillis))
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
        }
        binding.filterTagsSelectorButton.setOnClickListener {
            if (filterViewModel.selectedTags.value != null && binding.filterTagsSelector.text.toString().trim().isNotEmpty())
            {
                if (!filterViewModel.selectedTags.value!!.contains(binding.filterTagsSelector.text.toString()))
                {
                    filterViewModel.selectedTags.value!!.add(binding.filterTagsSelector.text.toString())
                    filterListViewAdapter.notifyDataSetChanged()
                }
            }
            binding.filterTagsSelector.setText("")
        }
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}