package com.cmpt362.nearby.database

import android.util.Log
import com.cmpt362.nearby.classes.Post
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreDatabase() {
    private val db = FirebaseFirestore.getInstance()

    fun getPost() {
        db.collection("posts")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    Log.d("firebase", "${document.data.values}")
                }
            }

    }

    fun addPost(entry: Post) {
        // TODO:add a Post class to .classes, and create a hashmap from the Post class parameters
    }
}