package com.ongo.signal.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface SignalApi {
    @Headers(
        "Content-Type: application/json",
        "accesstoken: asda13"
    )
    @GET("v1/main/gogo")
    suspend fun getMainPost(id: Int): Response<Int>
}