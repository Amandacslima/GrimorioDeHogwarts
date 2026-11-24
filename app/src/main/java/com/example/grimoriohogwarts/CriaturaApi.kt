package com.example.grimoriohogwarts

import retrofit2.http.GET
import retrofit2.http.Query

interface CriaturaApi {
    @GET("api/creatures")
    suspend fun getCriaturaImage(@Query("search") search: String): CriaturaResponse
}