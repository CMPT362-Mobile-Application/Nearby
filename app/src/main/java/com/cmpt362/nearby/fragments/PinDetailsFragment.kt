package com.cmpt362.nearby.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cmpt362.nearby.R
import com.cmpt362.nearby.activities.CommentActivity
import com.cmpt362.nearby.classes.GlideApp
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.databinding.FragmentPinDetailsBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PinDetailsFragment(val post: Post, val id: String) : Fragment(R.layout.fragment_pin_details) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPinDetailsBinding.inflate(inflater, container, false)

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

        return binding.root
    }
}