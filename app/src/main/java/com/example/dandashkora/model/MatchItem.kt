package com.example.dandashkora.model

data class MatchItem(
    val date: String,
    val time: String,
    val home_team: Team,
    val away_team: Team,
    val league: String,
    val channel: String
)
