package com.cmpt362.nearby.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class DbFilter private constructor (
    // uses the builder pattern described in https://www.baeldung.com/kotlin/builder-pattern

    var tags: ArrayList<String>,
    var earliestTime: Timestamp?,
    var latestTIme: Timestamp?) {

    data class Builder (
        var tags: ArrayList<String> = arrayListOf(),
        var earliestTime: Timestamp? = null,
        var latestTIme: Timestamp? = null) {

        fun tags(tags: ArrayList<String>) = apply { this.tags = tags }
        fun earliest(earliestTime: Timestamp) = apply { this.earliestTime = earliestTime}
        fun latest(latestTIme: Timestamp) = apply { this.latestTIme = latestTIme}
        fun build() = DbFilter(tags, earliestTime, latestTIme)
    }

    fun getQuery(collection: CollectionReference): Query {
        var query: Query = collection

        if (tags.isNotEmpty()) {
            query = query.whereArrayContains("tag", tags)
        }
        if (earliestTime != null) {
            query = query.whereGreaterThanOrEqualTo("startTime", earliestTime!!)
        }
        if (latestTIme != null) {
            query = query.whereLessThanOrEqualTo("startTime", latestTIme!!)
        }

        return query
    }

}