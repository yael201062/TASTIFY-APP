package com.example.tastify.data.api

data class PexelsResponse(
    val photos: List<PexelsPhoto>
)

data class PexelsPhoto(
    val src: PexelsSrc
)

data class PexelsSrc(
    val original: String
)
