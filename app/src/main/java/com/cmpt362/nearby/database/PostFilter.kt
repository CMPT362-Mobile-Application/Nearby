package com.cmpt362.nearby.database

import com.cmpt362.nearby.classes.Post
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class PostFilter private constructor (
    // uses the builder pattern described in https://www.baeldung.com/kotlin/builder-pattern

    private var tags: ArrayList<String>,
    private var earliestTime: Timestamp?,
    private var latestTIme: Timestamp?,
    private var excludeIds: ArrayList<String>,
    private var includeIds: ArrayList<String>) {

    data class Builder (
        var tags: ArrayList<String> = arrayListOf(),
        var earliestTime: Timestamp? = null,
        var latestTime: Timestamp? = null,
        var excludeIds: ArrayList<String> = arrayListOf(),
        var includeIds: ArrayList<String> = arrayListOf()) {

        fun tags(tags: ArrayList<String>) = apply { this.tags = tags }
        fun earliest(earliestTime: Timestamp?) = apply { this.earliestTime = earliestTime}
        fun latest(latestTime: Timestamp?) = apply { this.latestTime = latestTime}

        /**
         * Posts with these ids will be first excluded during the filter
         */
        fun excludeIds(ids: ArrayList<String>) = apply { this.excludeIds = ids }

        /**
         * Posts must have these ids to be included after the filter
         */
        fun includeIds(ids: ArrayList<String>) = apply { this.includeIds = ids }
        fun build() = PostFilter(tags, earliestTime, latestTime, excludeIds, includeIds)
    }

    fun filter(idPostPairs: ArrayList<Pair<String, Post>>): ArrayList<Pair<String, Post>> {
        val predicates = ArrayList< (Pair<String, Post>) -> Boolean >()

        if (includeIds.isNotEmpty()) {
            if (excludeIds.isNotEmpty()) {
                includeIds.removeAll {
                    it in excludeIds
                }
            }
            // check if the id is in the ids we want to include
            predicates.add { it.first in includeIds }
        } else if (excludeIds.isNotEmpty()) {
            predicates.add { it.first !in excludeIds }
        }

        // filter predicates based on post field parameters
        if (tags.isNotEmpty()) {
            predicates.add { it.second.tag in tags }
        }
        if (earliestTime != null) {
            predicates.add { it.second.startTime.seconds >= earliestTime!!.seconds }
        }
        if (latestTIme != null) {
            predicates.add { it.second.startTime.seconds <= latestTIme!!.seconds }
        }

        return idPostPairs.filter { pair ->
            predicates.all { it(pair) }
        } as ArrayList<Pair<String, Post>>
    }

}