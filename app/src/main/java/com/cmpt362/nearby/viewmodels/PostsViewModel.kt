package com.cmpt362.nearby.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.database.FirestoreDatabase
import com.google.firebase.firestore.CollectionReference

class PostsViewModel {
    val postsList: MutableLiveData<ArrayList<Post>> =
        MutableLiveData(arrayListOf())
    private lateinit var docRef: CollectionReference

    init {
        val firestore = FirestoreDatabase()
        docRef = firestore.getPost()
        docRef.get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot) {
                postsList.value?.add(document.toObject(Post::class.java))
            }
            //println(postsList.value?.get(0)?.title)
        }
    }
}