package com.example.dandashkora.network

import com.example.dandashkora.model.MatchResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("https://football-apis-five.vercel.app/api/matches")
    suspend fun getMatches(): Response<MatchResponse>
}
