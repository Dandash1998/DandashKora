package com.example.dandashkora.model

import com.example.dandashkora.model.MatchItem

data class MatchResponse(
    val last_updated: String,
    val matches: List<MatchItem>,
    val channels: Map<String, Map<String, Map<String, String>>>
)
