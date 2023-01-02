package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class PostEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val pubkey: String,
    val replyTo: String?,
    val replyToRoot: String?,
    val repostedId: String?,
    val content: String,
    val createdAt: Long,
)
