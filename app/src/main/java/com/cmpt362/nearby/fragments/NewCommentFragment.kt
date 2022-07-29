package com.cmpt362.nearby.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.databinding.FragmentNewCommentBinding
import com.cmpt362.nearby.viewmodels.CommentViewModel

class NewCommentFragment() : Fragment(R.layout.fragment_new_comment) {
    private var _binding: FragmentNewCommentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewCommentBinding.inflate(inflater, container, false)

        val commentViewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
        commentViewModel.replyingTo.observe(viewLifecycleOwner) {
            setReplyingTo(it)
        }

        binding.newCommentReply.setOnClickListener {
            if (it.isVisible) { commentViewModel.replyingTo.value = null }
        }
        binding.newCommentBtn.setOnClickListener {
//            commentViewModel.addComment(binding.newCommentEdittext.text.toString())
        }

        return binding.root
    }


    fun setReplyingTo(id: String?) {
        if (id != null) {
            binding.newCommentBtn.text = getString(R.string.reply)
            binding.newCommentReply.text = String.format("X replying to %s", id)
            binding.newCommentReply.visibility = View.VISIBLE
        } else {
            binding.newCommentBtn.text = getString(R.string.post)
            binding.newCommentReply.text = ""
            binding.newCommentReply.visibility = View.INVISIBLE
        }
    }

}