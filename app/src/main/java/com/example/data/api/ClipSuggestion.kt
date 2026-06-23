package com.example.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClipSuggestion(
    val title: String,
    val startTime: String,
    val endTime: String,
    val viralScore: Int,
    val justification: String
)
