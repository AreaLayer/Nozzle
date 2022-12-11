package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    suspend fun getProfile(pubkey: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg profiles: ProfileEntity)
}