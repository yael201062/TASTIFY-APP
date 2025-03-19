package com.example.tastify.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenTableApiClient {
    val apiService: OpenTableApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://opentable.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenTableApiService::class.java)
    }
}
