package com.cmpt362.nearby.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.nearby.R
import com.cmpt362.nearby.classes.GlideApp
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.classes.Util
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text
import java.io.IOException

class FavouriteListAdapter(val context: Context, private var favPosts: ArrayList<Pair<String, Post>>) : BaseAdapter() {
    override fun getCount(): Int {
        return favPosts.size
    }

    override fun getItem(position: Int): Pair<String, Post> {
        return favPosts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateItems(items: ArrayList<Pair<String, Post>>) {
        favPosts = items
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.adapter_favourite, null)
        val imageView = view.findViewById<ImageView>(R.id.favouriteAdapter_imageView)
        val postTitle = view.findViewById<TextView>(R.id.favouriteAdapter_postTitle)
        val postLocation = view.findViewById<TextView>(R.id.favouriteAdapter_postLocation)
        val postDateTime = view.findViewById<TextView>(R.id.favouriteAdapter_postDateTime)
        val post = favPosts[position].second

        // Read data from post
        if (!post.imageUrl.equals("null") && post.imageUrl.isNotBlank()) {
            val storage = Firebase.storage("gs://cmpt362-nearby")
            val storageRef = storage.reference
            val imageRef = storageRef.child(post.imageUrl)

            GlideApp.with(view)
                .load(imageRef)
                .placeholder(R.drawable.ic_launcher_background)
                .fallback(ColorDrawable(Color.TRANSPARENT))
                .into(imageView)
        } else {
            imageView.visibility = ImageView.GONE
        }

        postTitle.text = post.title

        try {
            val geocoder = Geocoder(context)
            val result = geocoder.getFromLocation(post.location.latitude, post.location.longitude, 1)
            if (result.size > 0)
                postLocation.text = result[0].getAddressLine(0)
            else
                postLocation.text = "${post.location.latitude}, ${post.location.longitude}"
        } catch (e: IOException) {
            postLocation.text = "${post.location.latitude}, ${post.location.longitude}"
        }

        // Set time
        if (post.event) {
            val startText = Util.timestampToDateStrPost(post.startTime)
            val endText = Util.timestampToDateStrPost(post.endTime)
            postDateTime.text = "$startText to $endText"
        } else {
            postDateTime.text = Util.timestampToDateStrPost(post.startTime)
        }

        return view
    }
}