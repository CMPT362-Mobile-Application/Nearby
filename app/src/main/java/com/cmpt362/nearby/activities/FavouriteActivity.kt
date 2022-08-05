package com.cmpt362.nearby.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.adapters.FavouriteListAdapter
import com.cmpt362.nearby.databinding.ActivityFavouriteBinding
import com.cmpt362.nearby.viewmodels.FavouriteViewModel

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var title: TextView
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var favouriteListAdapter: FavouriteListAdapter
    private lateinit var listView: ListView

    companion object {
        const val MYPOSTS_KEY = "myposts"
        const val FAVOURITES_KEY = "favourites"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        leftButton = binding.favouriteLeftButton
        rightButton = binding.favouriteRightButton
        title = binding.favouriteTitle

        favouriteViewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        favouriteViewModel.state.observe(this) {
            if (it == MYPOSTS_KEY) {
                // Update buttons and title
                leftButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_left_active)
                rightButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_right_inactive)
                title.text = getString(R.string.favourites_myposts)

                // Update ListView
            } else if (it == FAVOURITES_KEY) {
                // Update buttons and title
                leftButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_left_inactive)
                rightButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_right_active)
                title.text = getString(R.string.favourites_favourites)

                // Update ListView
            } else {
                finish() // Unknown state, close activity
            }
        }

        // Left Button will change state if needed
        leftButton.setOnClickListener {
            if (favouriteViewModel.state.value == FAVOURITES_KEY)
                favouriteViewModel.state.value = MYPOSTS_KEY
        }

        // Right Button will change state if needed
        rightButton.setOnClickListener {
            if (favouriteViewModel.state.value == MYPOSTS_KEY)
                favouriteViewModel.state.value = FAVOURITES_KEY
        }

        // Initiate List View
        listView = binding.favouriteListView
        favouriteListAdapter = FavouriteListAdapter(this)
        listView.adapter = favouriteListAdapter

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