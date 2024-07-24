package com.ongo.signal.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface SignalApi {
    @Headers(
        "Content-Type: application/json",
        "accesstoken: asda13"
    )
    @GET("v1/main/gogo")
    suspend fun getMainPost(id: Int): Response<Int>

    @POST("location")
    suspend fun postMatchRegistration(
        @Body latitude: Double,
        @Body longitude: Double,
        @Body user_id: Long
    ): Response<Int>

}