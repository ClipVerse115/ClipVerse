package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyzed_videos")
data class AnalyzedVideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val youtubeUrl: String,
    val videoTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val clipsJson: String // Serialized List<ClipSuggestion> JSON
)
