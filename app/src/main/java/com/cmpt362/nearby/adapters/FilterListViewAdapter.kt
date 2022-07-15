package com.cmpt362.nearby.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.cmpt362.nearby.databinding.AdapterFilterTagsListBinding

class FilterListViewAdapter(private val context: Context,
                            private var data: ArrayList<String>): BaseAdapter() {

    private var _binding: AdapterFilterTagsListBinding? = null
    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val binding get() = _binding!!

    override fun getCount(): Int {
        return data.count()
    }

    override fun getItem(p0: Int): String {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        _binding = AdapterFilterTagsListBinding.inflate(inflater,p2,false)
        binding.filterListViewTag.text = getItem(p0)
        binding.filterListViewDeleteButton.setOnClickListener {
            data.removeAt(p0)
            notifyDataSetChanged()
        }
        return binding.root
    }
}