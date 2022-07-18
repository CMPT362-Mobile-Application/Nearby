package com.cmpt362.nearby.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.adapters.CommentListAdapter
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.databinding.ActivityCommentBinding
import com.cmpt362.nearby.fragments.NewCommentFragment
import com.cmpt362.nearby.viewmodels.CommentViewModel

class CommentActivity : AppCompatActivity(){
    private lateinit var binding: ActivityCommentBinding
    private lateinit var commentViewModel: CommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newCommentFragment = NewCommentFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.new_comment_fragment_container, newCommentFragment).commit()

        binding = ActivityCommentBinding.inflate(layoutInflater)

        commentViewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
        var commentListAdapter = commentViewModel.commentList.value?.let {
            CommentListAdapter(it, newCommentFragment::setReplyingTo) }
        binding.commentRecycler.adapter = commentListAdapter

        // set comments adapter to change whenever value in viewModel is updated
        commentViewModel.commentList.observe(this) { comments ->
            comments?.let {
                commentViewModel.commentList.value?.let {
                    commentListAdapter?.updateItems(comments)
                    binding.commentRecycler.adapter = commentListAdapter
                }
            }
        }

        setContentView(binding.root)
    }
}