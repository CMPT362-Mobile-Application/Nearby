package com.cmpt362.nearby.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.cmpt362.nearby.viewmodels.CommentViewModel
import com.cmpt362.nearby.viewmodels.PostsViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class PinDetailsFragment(val post: Post, val id: String) : Fragment(R.layout.fragment_pin_details) {
    private val commentViewModel: CommentViewModel by viewModels {
        Util.CommentViewModelFactory(id)
    }


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

        // Load the image
        println("debug: ${post.imageUrl}")
        if (!post.imageUrl.equals("null") && post.imageUrl.isNotBlank()) {
            val storage = Firebase.storage("gs://cmpt362-nearby")
            val storageRef = storage.reference
            val imageRef = storageRef.child(post.imageUrl)

            GlideApp.with(this)
                .load(imageRef)
                .placeholder(R.drawable.ic_launcher_background)
                .fallback(ColorDrawable(Color.TRANSPARENT))
                .into(binding.pinDetailPostImage)
        } else {
            binding.pinDetailPostImage.visibility = ImageView.GONE // Hide the image if none
        }

        // Set the title
        binding.pinDetailTitle.text = post.title

        // Set the location
        val geocoder = Geocoder(requireActivity())
        val result = geocoder.getFromLocation(post.location.latitude, post.location.longitude, 1)
        if (result.size > 0)
            binding.pinDetailLocation.text = result[0].getAddressLine(0)
        else
            binding.pinDetailLocation.text = "${post.location.latitude}, ${post.location.longitude}"

        // Set time
        if (post.isEvent) {
            val startText = Util.timestampToDateStr(post.startTime)
            val endText = Util.timestampToDateStr(post.endTime)
            binding.pinDetailTime.text = "$startText to $endText"
        } else {
            binding.pinDetailTime.text = Util.timestampToDateStr(post.startTime)
        }

        // Set description
        binding.pinDetailDescription.text = post.info
        //
        binding.pinDetailLikeText.text = "${post.favouritesCounter}"

        commentViewModel.commentList.observe(requireActivity()) {
            val size = it.size
            binding.pinDetailCommentText.text = if (size == 0) {
                "No one has commented yet!"
            } else {
                it[it.size - 1].info
            }
        }

        binding.pinDetailLikeButton.setOnClickListener() {
            val sharedPref = activity?.getSharedPreferences("favourites", Context.MODE_PRIVATE)
            if (sharedPref != null) {
                if (sharedPref.contains(id)) { // un favourite post
                    FirestoreDatabase.changeFavouriteCounter(id, -1)
                    binding.pinDetailLikeText.text = "${post.favouritesCounter}"
                } else { // favourite post
                    FirestoreDatabase.changeFavouriteCounter(id, 1)
                    binding.pinDetailLikeText.text = "${post.favouritesCounter + 1}"
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