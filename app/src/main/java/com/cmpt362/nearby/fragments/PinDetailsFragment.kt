package com.cmpt362.nearby.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.activities.CommentActivity
import com.cmpt362.nearby.classes.GlideApp
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.classes.Util
import com.cmpt362.nearby.database.FirestoreDatabase
import com.cmpt362.nearby.databinding.FragmentPinDetailsBinding
import com.cmpt362.nearby.viewmodels.FavouriteViewModel
import com.cmpt362.nearby.viewmodels.PostsViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class PinDetailsFragment(val post: Post, val id: String) : Fragment(R.layout.fragment_pin_details) {

    private lateinit var postsViewModel: PostsViewModel
    private var canAddFavourite = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPinDetailsBinding.inflate(inflater, container, false)
        postsViewModel = ViewModelProvider(this).get(PostsViewModel::class.java)

        binding.pinDetailComment.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            intent.putExtra("postId", id)
            startActivity(intent)
        }

        // Image load test
        println("debug: ${post.imageUrl}")
        if (!post.imageUrl.equals("null") && post.imageUrl.isNotBlank()) {
            val storage = Firebase.storage("gs://cmpt362-nearby")
            val storageRef = storage.reference
            val imageRef = storageRef.child(post.imageUrl)

            GlideApp.with(this)
                .load(imageRef)
                .placeholder(R.drawable.funnel)
                .into(binding.pinDetailPostImage)
        }

        binding.pinDetailLikeButton.setOnClickListener() {
            val sharedPref = activity?.getSharedPreferences("favourites", Context.MODE_PRIVATE)
            if (sharedPref != null) {
                if (sharedPref.contains(id)) { // un favourite post
                    FirestoreDatabase.decrementFavouritePost(id)
                } else { // favourite post
                    FirestoreDatabase.incrementFavouritePost(id)
                }
                canAddFavourite = true
            }
        }

        // check if we already favourited the post
        val sharedPref = activity?.getSharedPreferences("favourites", Context.MODE_PRIVATE)
        val likeButton: ImageButton = binding.pinDetailLikeButton
        if (sharedPref != null) {
            if (sharedPref.contains(id)) {
                likeButton.setImageResource(R.drawable.heart_red)
            }
        }

        postsViewModel.postsList.observe(viewLifecycleOwner) {
            if (canAddFavourite) {
                Log.i("favouritesListener", "postsViewModel has been updated")

                val sharedPref = activity?.getSharedPreferences("favourites", Context.MODE_PRIVATE)
                val likeButton: ImageButton = binding.pinDetailLikeButton
                if (sharedPref != null) {
                    if (sharedPref.contains(id)) { // un favourite post
                        with (sharedPref.edit()) {
                            remove(id)
                            apply()
                        }
                        likeButton.setImageResource(R.drawable.heart_grey)
                    } else { // favourite post
                        with (sharedPref.edit()) {
                            putString(id, id)
                            apply()
                        }
                        likeButton.setImageResource(R.drawable.heart_red)
                    }
                }
                canAddFavourite = false
            }
        }

        return binding.root
    }
}