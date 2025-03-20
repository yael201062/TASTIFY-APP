package com.example.tastify.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PexelsApiClient {
    private const val BASE_URL = "https://api.pexels.com/v1/"

    val apiService: PexelsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PexelsApiService::class.java)
    }
}
