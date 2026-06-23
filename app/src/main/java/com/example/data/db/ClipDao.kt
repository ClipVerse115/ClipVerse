package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipDao {
    @Query("SELECT * FROM analyzed_videos ORDER BY timestamp DESC")
    fun getAllAnalyzedVideos(): Flow<List<AnalyzedVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyzedVideo(video: AnalyzedVideoEntity): Long

    @Query("DELETE FROM analyzed_videos WHERE id = :id")
    suspend fun deleteVideoById(id: Long)

    @Query("DELETE FROM analyzed_videos")
    suspend fun clearAllHistory()
}
