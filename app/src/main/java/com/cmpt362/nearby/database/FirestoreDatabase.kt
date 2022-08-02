package com.cmpt362.nearby.database

import android.util.Log
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.classes.Post
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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

    fun getPosts(): CollectionReference {
        return db.collection(POSTS)
        // apply a default filter if none is set
//        if (filter == null) {
//            filter = DbFilter.Builder().build()
//        }
//
//        val posts = arrayListOf<Post>()
//        filter.getQuery(db.collection(POSTS))
//            .get()
//            .addOnSuccessListener {
//                for (document in it) {
//                    posts.add(document.toObject(Post::class.java))
//                }
//            }
        //            .addOnSuccessListener {
        //                val firestorePosts: ArrayList<Post> = arrayListOf()
        //                for (document in it) {
        //                    val post = document.toObject(Post::class.java)
        //                    firestorePosts.add(post)
        //                    Log.d("firebase", "$post")
        //                }
        //                return@addOnSuccessListener
        //            }
        //            .addOnFailureListener { e ->
        //                Log.w("firebase", "Error receiving posts", e)
        //            }
    }

    fun addPost(entry: Post) {
        db.collection("posts")
            .add(entry)
            .addOnSuccessListener { documentReference ->
               
                val postId="${documentReference.id}"
                Log.d("firebase", "DocumentSnapshot written with ID: $postId")
                
                // when creating a new post, also make a new document for comments with the same id
                db.collection(COMMENTS).document(postId).set(
                    hashMapOf(
                        COMMENT_COUNT to 0L,
                        COMMENT_ITEMS to arrayListOf<Comment>()
                    )
                )}
            .addOnFailureListener { e ->
                Log.w("firebase", "Error adding document", e)
            }


    }

    fun incrementFavouritePost(postId: String) {
        val postRef = db.collection(POSTS).document(postId)
        postRef.update("favouritesCounter", FieldValue.increment(1))
            .addOnSuccessListener { document ->
                Log.d("incrementFavouritePost", "$document")
            }
            .addOnFailureListener { exception ->
                Log.d("incrementFavouritePost", "failed with", exception)
            }
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