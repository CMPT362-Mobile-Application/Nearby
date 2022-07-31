package com.cmpt362.nearby.database

import android.util.Log
import com.cmpt362.nearby.classes.Post
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreDatabase() {
    private val db = FirebaseFirestore.getInstance()

    fun getPost(): CollectionReference {
        return db.collection("posts")
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
        // TODO:add a Post class to .classes, and create a hashmap from the Post class parameters
        db.collection("posts")
            .add(entry)
            .addOnSuccessListener { documentReference ->
                Log.d("firebase", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("firebase", "Error adding document", e)
            }
    }
}