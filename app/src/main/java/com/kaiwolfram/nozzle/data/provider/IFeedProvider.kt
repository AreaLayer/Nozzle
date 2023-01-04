package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta

interface IFeedProvider {
    suspend fun getFeed(): List<PostWithMeta>
    suspend fun getLatestTimestamp(): Long?
    suspend fun getFeedWithSingleAuthor(pubkey: String): List<PostWithMeta>
}
