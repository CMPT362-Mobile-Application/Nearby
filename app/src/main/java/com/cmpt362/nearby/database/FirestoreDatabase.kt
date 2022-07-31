package com.cmpt362.nearby.database

import android.util.Log
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.classes.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class FirestoreDatabase {
    private val db = FirebaseFirestore.getInstance()
    lateinit var filter: DbFilter

    private val COMMENTS = "comments"
    private val POSTS = "posts"
    private val COMMENT_COUNT = "counter"
    private val COMMENT_ITEMS = "items"

    fun getPosts() {
        // apply a default filter if none is set
        if (this::filter.isInitialized) {
            filter = DbFilter.Builder().build()
        }

        val posts = arrayListOf<Post>()
        filter.getQuery(db.collection(POSTS))
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    posts.add(document.toObject(Post::class.java))
                }
            }
    }

    fun addPost(entry: Post) {
        val postId=""
        // when creating a new post, also make a new document for comments with the same id
        db.collection(COMMENTS).document(postId).set(
            hashMapOf(
                COMMENT_COUNT to 0L,
                COMMENT_ITEMS to arrayListOf<Comment>()
            )
        )

    }


    fun getComments(postId: String, callback: (ArrayList<Comment>) -> Unit) {
        lateinit var comments: ArrayList<Comment>
        db.collection(COMMENTS).document(postId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val commentList = document.toObject(CommentList::class.java)
                    callback(commentList!!.items)
                }
                Log.d("getComment", "${document.data?.get(COMMENT_COUNT)}")
            }
            .addOnFailureListener { exception ->
                callback(arrayListOf())
                Log.d("getComment", "failed with", exception)
            }
    }

    fun addComment(comment: Comment, postId: String) {
        val commentRef = db.collection(COMMENTS).document(postId)
        commentRef.get().addOnSuccessListener {
            val id = it.data?.get(COMMENT_COUNT) as Long

            commentRef.update(COMMENT_ITEMS, FieldValue.arrayUnion(
                hashMapOf(
                    "id" to id,
                    "info" to comment.info,
                    "timestamp" to comment.timestamp,
                    "replyId" to comment.replyId)))

            commentRef.update(COMMENT_COUNT, FieldValue.increment(1))
        }

    }


    private data class CommentList (
        val itemCount: Long = 0L,
        val items: ArrayList<Comment> = arrayListOf()
    )
}