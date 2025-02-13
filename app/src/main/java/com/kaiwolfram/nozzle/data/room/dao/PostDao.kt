package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.RepostPreview


@Dao
interface PostDao {

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE pubkey IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey) " +
                "AND (:until IS NULL OR createdAt < :until) " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getLatestFeed(pubkey: String, limit: Int, until: Long?): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE pubkey IN (:contactPubkeys) " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getLatestFeedOfCustomContacts(
        vararg contactPubkeys: String,
        limit: Int = 100
    ): List<PostEntity>

    @Query(
        "SELECT MAX(createdAt) " +
                "FROM post " +
                "WHERE pubkey IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey) "
    )
    suspend fun getLatestTimestampOfFeed(pubkey: String): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotPresent(vararg post: PostEntity)

    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE (:replyToRootId IS NOT NULL AND replyToRootId = :replyToRootId) " + // All
                "OR replyToId = :currentPostId " + // All replies to current post
                "OR id = :currentPostId " + // Current post
                "OR (:replyToId IS NOT NULL AND id = :replyToId) " + // Direct parent
                "OR (:replyToRootId IS NOT NULL AND id = :replyToRootId)" // Root post
    )
    suspend fun getWholeThread(
        currentPostId: String,
        replyToRootId: String?,
        replyToId: String?
    ): List<PostEntity>

    @MapInfo(keyColumn = "id")
    @Query(
        "SELECT * " +
                "FROM post " +
                "JOIN profile ON post.pubkey = profile.pubkey " +
                "WHERE id IN (:postIds) "
    )
    suspend fun getRepostsPreviewMap(postIds: List<String>): Map<String, RepostPreview>

    @MapInfo(keyColumn = "repostedId", valueColumn = "repostCount")
    @Query(
        "SELECT repostedId, COUNT(*) AS repostCount " +
                "FROM post " +
                "WHERE repostedId IN (:postIds) " +
                "GROUP BY repostedId"
    )
    suspend fun getNumOfRepostsPerPost(postIds: List<String>): Map<String, Int>

    @MapInfo(keyColumn = "replyToId", valueColumn = "replyCount")
    @Query(
        "SELECT replyToId, COUNT(*) AS replyCount " +
                "FROM post " +
                "WHERE replyToId IN (:postIds) " +
                "GROUP BY replyToId"
    )
    suspend fun getNumOfRepliesPerPost(postIds: List<String>): Map<String, Int>

    @Query(
        "SELECT repostedId " +
                "FROM post " +
                "WHERE pubkey = :pubkey " +
                "AND repostedId IN (:postIds)"
    )
    suspend fun listRepostedByPubkey(pubkey: String, postIds: List<String>): List<String>
}
