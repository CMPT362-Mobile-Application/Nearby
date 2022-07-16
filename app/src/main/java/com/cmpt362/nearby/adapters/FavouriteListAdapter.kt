package com.cmpt362.nearby.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.nearby.R
import org.w3c.dom.Text

class FavouriteListAdapter(val context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.adapter_favourite, null)
        val imageView = view.findViewById<ImageView>(R.id.favouriteAdapter_imageView)
        val postTitle = view.findViewById<TextView>(R.id.favouriteAdapter_postTitle)
        val postLocation = view.findViewById<TextView>(R.id.favouriteAdapter_postLocation)
        val postDateTime = view.findViewById<TextView>(R.id.favouriteAdapter_postDateTime)

        // Insert Dummy data
        imageView.setImageResource(R.drawable.smashultimate)
        postTitle.text = "Super Smash Bros. Ultimate Showdown Tournament"
        postLocation.text = "8888 999 Street"
        postDateTime.text = "14:30 pst. July 14, 2022"

        return view
    }
}