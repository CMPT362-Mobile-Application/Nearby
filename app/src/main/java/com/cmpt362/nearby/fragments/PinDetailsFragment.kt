package com.cmpt362.nearby.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpt362.nearby.R
import com.cmpt362.nearby.activities.CommentActivity
import com.cmpt362.nearby.databinding.FragmentPinDetailsBinding
import com.cmpt362.nearby.database.FirestoreDatabase

class PinDetailsFragment() : Fragment(R.layout.fragment_pin_details) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPinDetailsBinding.inflate(inflater, container, false)

        binding.pinDetailComment.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            intent.putExtra("postId", "PlHWFSl0brbE2RrcBTTo")
            startActivity(intent)
        }

        binding.pinDetailLikeButton.setOnClickListener() {
            FirestoreDatabase().incrementFavouritePost("JK3GBqFLW0jU6GCh8GjS")
        }

        return binding.root
    }
}