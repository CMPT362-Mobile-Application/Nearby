package com.cmpt362.nearby.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.adapters.FavouriteListAdapter
import com.cmpt362.nearby.classes.Post
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
    private lateinit var sharedPrefUserID: SharedPreferences
    private lateinit var favouriteListAdapter: FavouriteListAdapter
    private lateinit var listView: ListView

    // For swipe left/right gesture
    private var x1: Float = 0f
    private var y1: Float = 0f
    private val minDistance: Float = 150f

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
        postsViewModel = ViewModelProvider(this)[PostsViewModel::class.java]
        postsViewModel.idPostPairs.observe(this) {
            favouriteViewModel.state.postValue(favouriteViewModel.state.value) // required as it forces an update
        }

        // Get shared prefs
        sharedPrefFavs = getSharedPreferences(FAVOURITES_KEY, Context.MODE_PRIVATE)
        sharedPrefUserID = getSharedPreferences(MYPOSTS_KEY, Context.MODE_PRIVATE)

        // Set up view model to remember state
        favouriteViewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        favouriteViewModel.state.observe(this) {
            if (it == MYPOSTS_KEY) {
                binding.favouriteListView.adapter = null
                // Update buttons and title
                leftButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_left_active)
                rightButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_right_inactive)
                title.text = getString(R.string.favourites_myposts)

                // Update ListView
                if (sharedPrefUserID != null && sharedPrefUserID.all.isNotEmpty()
                    && postsViewModel.idPostPairs.value != null) {
                    // According to PinDetailsFragment, the favourite ids can be found both in keys and values
                    val userID = sharedPrefUserID.all.keys
                    favouriteViewModel.loadMyPosts(userID, postsViewModel.idPostPairs.value!!)
                } else {
                    binding.favouriteNoneFound.visibility = TextView.VISIBLE
                }

            } else if (it == FAVOURITES_KEY) {
                binding.favouriteListView.adapter = null
                // Update buttons and title
                leftButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_left_inactive)
                rightButton.background = AppCompatResources.getDrawable(this, R.drawable.favourite_button_right_active)
                title.text = getString(R.string.favourites_favourites)

                // Update ListView
                if (sharedPrefFavs != null && sharedPrefFavs.all.isNotEmpty()
                    && postsViewModel.idPostPairs.value != null) {
                    // According to PinDetailsFragment, the favourite ids can be found both in keys and values
                    val favPostIds = sharedPrefFavs.all.keys
                    favouriteViewModel.loadFavouritePosts(favPostIds, postsViewModel.idPostPairs.value!!)
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
        favouriteViewModel.myPosts.observe(this) {
            if (it != null && it.isNotEmpty() && favouriteViewModel.myPostIds.value!!.isNotEmpty()) {
                binding.favouriteNoneFound.visibility = TextView.GONE
                val ids = favouriteViewModel.myPostIds.value!!

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

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y
            }
            MotionEvent.ACTION_UP -> {
                val x2 = event.x
                val y2 = event.y
                val xDiff = x2 - x1
                val yDiff = y2 - y1
                if (Math.abs(xDiff) > Math.abs(yDiff) && Math.abs(xDiff) > minDistance) {
                    if (xDiff > 0) { // Swipe to right
                        if (favouriteViewModel.state.value == FAVOURITES_KEY)
                            favouriteViewModel.state.value = MYPOSTS_KEY
                    } else { // Swipe to left
                        if (favouriteViewModel.state.value == MYPOSTS_KEY)
                            favouriteViewModel.state.value = FAVOURITES_KEY
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}