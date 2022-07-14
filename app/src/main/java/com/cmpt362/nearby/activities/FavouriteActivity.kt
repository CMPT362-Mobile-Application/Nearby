package com.cmpt362.nearby.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.databinding.ActivityFavouriteBinding
import com.cmpt362.nearby.viewmodels.FavouriteViewModel

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var title: TextView
    private lateinit var favouriteViewModel: FavouriteViewModel

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

        setContentView(binding.root)
    }
}