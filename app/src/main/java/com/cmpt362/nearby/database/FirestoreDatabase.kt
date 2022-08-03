package com.cmpt362.nearby.database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.classes.Post
import com.google.firebase.firestore.*


object FirestoreDatabase {
    private val COMMENTS = "comments"
    private val POSTS = "posts"
    private val COMMENT_COUNT = "counter"
    private val COMMENT_ITEMS = "items"

    private val POST_LISTENER_KEY = "POST_LISTENER"
    private val COMMENT_LISTENER_KEY = "COMMENT_LISTENER"
    private val FAVOURITE_LISTENER_KEY = "FAVOURITE_LISTENER"
    private val listeners = HashMap<String, ListenerRegistration>()

    fun registerFavouritesListener(
        postId: String,
        callback: (String) -> Unit) {
        restartFavouritesListener(postId, callback)
    }

    private fun restartFavouritesListener(postId: String, callback: (String) -> Unit) {
        if (listeners.containsKey(FAVOURITE_LISTENER_KEY)) {
            listeners[FAVOURITE_LISTENER_KEY]!!.remove()
            listeners.remove(FAVOURITE_LISTENER_KEY)
        }

        val favouritesListener = FirebaseFirestore.getInstance().collection(POSTS).document(postId)
            .addSnapshotListener { document, _ ->
                if (document != null) {
                    Log.i("favouritesListener", "FirestoreDatabase: " + document.id)
                    callback(document.id)
                }
            }

        listeners[FAVOURITE_LISTENER_KEY] = favouritesListener
    }

    fun incrementFavouritePost(postId: String) {
        val postRef = FirebaseFirestore.getInstance().collection(POSTS).document(postId)
        postRef.update("favouritesCounter", FieldValue.increment(1))
            .addOnSuccessListener {
                Log.d("incrementFavouritePost", "incrementFavouritePost worked with $postId")
            }
            .addOnFailureListener { exception ->
                Log.d("incrementFavouritePost", "incrementFavouritePost failed with", exception)
            }
    }

    fun decrementFavouritePost(postId: String) {
        val postRef = FirebaseFirestore.getInstance().collection(POSTS).document(postId)
        postRef.update("favouritesCounter", FieldValue.increment(-1))
            .addOnSuccessListener { document ->
                //Log.d("incrementFavouritePost", "$document")
            }
            .addOnFailureListener { exception ->
                //Log.d("incrementFavouritePost", "failed with", exception)
            }
    }

    fun registerPostsListener(
        filter: DbFilter,
        callback: (ArrayList<Post>, ArrayList<String>) -> Unit) {

        restartPostsListener(POST_LISTENER_KEY, filter, callback)
    }

    private fun restartPostsListener(
        key: String,
        filter: DbFilter,
        callback: (ArrayList<Post>, ArrayList<String>) -> Unit) {

        if (listeners.containsKey(key)) {
            listeners[key]!!.remove()
            listeners.remove(key)
        }

        val postListener = filter.getQuery(FirebaseFirestore.getInstance().collection(POSTS))
            .addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null) {
                val resultList = arrayListOf<Post>()
                val docIdList = arrayListOf<String>()
                for (document in documentSnapshot) {
                    resultList.add(document.toObject(Post::class.java))
                    docIdList.add(document.id)
                }

                callback(resultList, docIdList)
            }
        }

        listeners[key] = postListener
    }


    fun addPost(entry: Post) {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .add(entry)
            .addOnSuccessListener { documentReference ->
                val postId = documentReference.id

                // when creating a new post, also make a new document for comments with the same id
                FirebaseFirestore.getInstance()
                    .collection(COMMENTS).document(postId).set(
                    hashMapOf(
                        COMMENT_COUNT to 0L,
                        COMMENT_ITEMS to arrayListOf<Comment>()
                    )
                )}
            .addOnFailureListener { e ->
                Log.w("firebase", "Error adding document", e)
            }

    }

    fun registerCommentsListener(postId: String, callback: (ArrayList<Comment>) -> Unit) {
        if (listeners.containsKey(COMMENT_LISTENER_KEY)) {
            listeners[COMMENT_LISTENER_KEY]!!.remove()
            listeners.remove(COMMENT_LISTENER_KEY)
        }

        val commentListener = FirebaseFirestore.getInstance().collection(COMMENTS).document(postId)
            .addSnapshotListener { document, _ ->
                if (document != null) {
                    val commentList = document.toObject(CommentList::class.java)
                    callback(commentList!!.items)
                }
            }

        listeners[COMMENT_LISTENER_KEY] = commentListener
    }


    fun addComment(comment: Comment, postId: String) {
        val commentRef = FirebaseFirestore.getInstance()
            .collection(COMMENTS).document(postId)

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