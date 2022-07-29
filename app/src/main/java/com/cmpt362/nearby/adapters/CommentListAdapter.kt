package com.cmpt362.nearby.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.databinding.AdapterCommentBinding

class CommentListAdapter(
    var comments: ArrayList<Comment>,
    val commentClickCallback: (Long) -> Unit
) : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    private lateinit var binding: AdapterCommentBinding

    class CommentViewHolder(private val binding: AdapterCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        binding = AdapterCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        binding.commentId.text = comment.id.toString()
        binding.commentTime.text = comment.timeStamp.toString()
        binding.commentInfo.text = comment.info

        // if no reply, make the replyId textview invisible
        if (comment.refId != Comment.NO_REF) {
            binding.commentReplyId.text = String.format("> %d", comment.refId)
        } else {
            binding.commentReplyId.visibility = View.GONE
        }

        binding.root.setOnClickListener {
            commentClickCallback(comment.id)
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateItems(newItems: ArrayList<Comment>?) {
        comments = newItems ?: arrayListOf()
        notifyDataSetChanged()
    }
}