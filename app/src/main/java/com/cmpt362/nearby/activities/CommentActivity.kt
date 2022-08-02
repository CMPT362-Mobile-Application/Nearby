package com.cmpt362.nearby.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.nearby.R
import com.cmpt362.nearby.adapters.CommentListAdapter
import com.cmpt362.nearby.classes.Util
import com.cmpt362.nearby.databinding.ActivityCommentBinding
import com.cmpt362.nearby.fragments.NewCommentFragment
import com.cmpt362.nearby.viewmodels.CommentViewModel

class CommentActivity : AppCompatActivity(){
    private lateinit var binding: ActivityCommentBinding
    private val postId: String by lazy {
        intent.extras?.getString("postId", "").toString()
    }

    // use lazy initialization
    private val commentViewModel: CommentViewModel by viewModels {
        Util.CommentViewModelFactory(postId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newCommentFragment = NewCommentFragment(postId)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.new_comment_fragment_container, newCommentFragment).commit()

        val commentListAdapter = if (commentViewModel.commentList.value != null) {
            CommentListAdapter(commentViewModel.commentList.value!!,
                newCommentFragment::setReplyingTo)
        } else {
            CommentListAdapter(arrayListOf(), newCommentFragment::setReplyingTo)
        }

        binding = ActivityCommentBinding.inflate(layoutInflater)
        binding.commentRecycler.adapter = commentListAdapter

        // set comments adapter to change whenever value in viewModel is updated
        commentViewModel.commentList.observe(this) { comments ->
                commentListAdapter.updateItems(comments)
        }

        setContentView(binding.root)
    }

}