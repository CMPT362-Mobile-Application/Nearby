package com.cmpt362.nearby.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.cmpt362.nearby.viewmodels.PostsViewModel

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var title: TextView
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var postsViewModel: PostsViewModel
    private lateinit var sharedPrefFavs: SharedPreferences
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

        // Enable Posts view model to get posts
        postsViewModel = ViewModelProvider(this).get(PostsViewModel::class.java)
        postsViewModel.postsList.observe(this) {
            favouriteViewModel.state.postValue(favouriteViewModel.state.value) // call to update
        }

        // Get shared prefs
        sharedPrefFavs = getSharedPreferences("favourites", Context.MODE_PRIVATE)

        // Set up view model to remember state
        favouriteViewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        favouriteViewModel.state.observe(this) {
            if (it == MYPOSTS_KEY) {
                // Update buttons and title
                leftButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_left_active)
                rightButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_right_inactive)
                title.text = getString(R.string.favourites_myposts)

                // Update ListView
                binding.favouriteNoneFound.visibility = TextView.VISIBLE
                binding.favouriteListView.adapter = null

            } else if (it == FAVOURITES_KEY) {
                // Update buttons and title
                leftButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_left_inactive)
                rightButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_right_active)
                title.text = getString(R.string.favourites_favourites)

                // Update ListView
                if (sharedPrefFavs != null && sharedPrefFavs.all.isNotEmpty()
                    && postsViewModel.postsList.value != null && postsViewModel.idList.value != null) {
                    // According to PinDetailsFragment, the favourite ids can be found both in keys and values
                    val favPostIds = sharedPrefFavs.all.keys
                    favouriteViewModel.loadFavouritePosts(favPostIds, postsViewModel.postsList.value!!, postsViewModel.idList.value!!)
                } else {
                    binding.favouriteNoneFound.visibility = TextView.VISIBLE
                }
            } else {
                finish() // Unknown state, close activity
            }
        }
        favouriteViewModel.favouritePosts.observe(this) {
            if (it != null && it.isNotEmpty() && favouriteViewModel.favouritePostIds.value!!.isNotEmpty()) {
                binding.favouriteNoneFound.visibility = TextView.GONE
                val ids = favouriteViewModel.favouritePostIds.value!!

                // Update ListView
                val favouritePostListAdapter = FavouriteListAdapter(this, it)
                binding.favouriteListView.adapter = favouritePostListAdapter
                binding.favouriteListView.setOnItemClickListener { parent, view, position, id ->
                    val returnIntent = Intent()
                    returnIntent.putExtra("id", ids[position]) // MapsActivity only needs id
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
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