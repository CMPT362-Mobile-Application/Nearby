package com.cmpt362.nearby.database

import android.util.Log
import com.cmpt362.nearby.classes.Comment
import com.cmpt362.nearby.classes.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.reflect.Array

object FirestoreDatabase {
    private val COMMENTS = "comments"
    private val POSTS = "posts"
    private val COMMENT_COUNT = "counter"
    private val COMMENT_ITEMS = "items"

    // increase the number of likes by changeAmount
    fun changeFavouriteCounter(postId: String, changeAmount: Long) {
        FirebaseFirestore.getInstance()
            .collection(POSTS)
            .document(postId)
            .update("favouritesCounter", FieldValue.increment(changeAmount))

    }

    fun getPosts(): Flow<ArrayList<Pair<String, Post>>> = callbackFlow {
        val listenerRegistration = FirebaseFirestore.getInstance()
            .collection(POSTS).addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val resultList = arrayListOf<Pair<String, Post>>()
                for (document in snapshot) {
                    resultList.add(Pair(document.id, document.toObject(Post::class.java)))
                }

                trySend(resultList).isSuccess
            }
        }
        awaitClose {
           listenerRegistration.remove()
        }
    }


    fun addPost(entry: Post): String {
        val db = FirebaseFirestore.getInstance()
        val postDocRef = db.collection(POSTS).document()
        postDocRef.set(entry)

        db.collection(COMMENTS).document(postDocRef.id).set(
            hashMapOf(
                COMMENT_COUNT to 0L,
                COMMENT_ITEMS to arrayListOf<Comment>()
            )
        )

        return postDocRef.id
    }


    fun getComments(postId: String): Flow<ArrayList<Comment>> = callbackFlow {
       val listenerRegistration = FirebaseFirestore.getInstance()
           .collection(COMMENTS).document(postId)
           .addSnapshotListener { snapshot, _ ->
               if (snapshot != null) {
                   val commentList = snapshot.toObject(CommentList::class.java)
                   trySend(commentList!!.items).isSuccess
               }
           }

        awaitClose {
            listenerRegistration.remove()
        }
    }


    fun addComment(comment: Comment, postId: String) {
        val commentRef = FirebaseFirestore.getInstance()
            .collection(COMMENTS).document(postId)

        commentRef.get().addOnSuccessListener {
            val id = it.data?.get(COMMENT_COUNT) as Long

            commentRef.update(
                COMMENT_ITEMS, FieldValue.arrayUnion(
                hashMapOf(
                    "id" to id,
                    "info" to comment.info,
                    "timestamp" to comment.timestamp,
                    "replyId" to comment.replyId)),
                COMMENT_COUNT, FieldValue.increment(1))
        }

    }


    private data class CommentList (
        val counter: Long = 0L,
        val items: ArrayList<Comment> = arrayListOf()
    )

}