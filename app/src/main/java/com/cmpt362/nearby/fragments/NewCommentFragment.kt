package com.cmpt362.nearby.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.cmpt362.nearby.R
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.classes.Util
import com.cmpt362.nearby.databinding.FragmentNewCommentBinding
import com.cmpt362.nearby.viewmodels.CommentViewModel

class NewCommentFragment(private val postId: String) : Fragment(R.layout.fragment_new_comment) {
    private var _binding: FragmentNewCommentBinding? = null
    private val binding get() = _binding!!
    private val commentViewModel: CommentViewModel by viewModels {
        Util.CommentViewModelFactory(postId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewCommentBinding.inflate(inflater, container, false)

        commentViewModel.replyingTo.observe(viewLifecycleOwner) {
            setReplyingTo(it)
        }

        binding.newCommentReply.setOnClickListener {
            if (it.isVisible) { commentViewModel.replyingTo.value = Comment.NO_REF }
        }
        binding.newCommentBtn.setOnClickListener {
            commentViewModel.addComment(binding.newCommentEdittext.text.toString())
            binding.newCommentEdittext.setText("")
            Toast.makeText(activity, "message sent", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }


    fun setReplyingTo(id: Long) {
        if (id != Comment.NO_REF) {
            binding.newCommentBtn.text = getString(R.string.reply)
            binding.newCommentReply.text = String.format("X replying to %d", id)
            binding.newCommentReply.visibility = View.VISIBLE
        } else {
            binding.newCommentBtn.text = getString(R.string.post)
            binding.newCommentReply.text = ""
            binding.newCommentReply.visibility = View.INVISIBLE
        }
    }

}