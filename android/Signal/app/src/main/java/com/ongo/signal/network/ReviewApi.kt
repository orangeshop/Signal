package com.ongo.signal.network

import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.data.model.review.ReviewResponseDTO
import com.ongo.signal.data.model.review.ReviewResponseItemDTO
import com.ongo.signal.data.model.review.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApi {

    @POST("review")
    suspend fun writeReview(@Body reviewRequestDTO: ReviewRequestDTO): Response<ReviewResponseItemDTO>

    @GET("review")
    suspend fun getReview(@Query("userId") userId: Long): Response<ReviewResponseDTO>

    @GET("user/{id}")
    suspend fun getUserProfile(@Path("id") id: Long): Response<UserProfileResponse>
}