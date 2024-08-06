package com.ongo.signal.network

import com.ongo.signal.data.model.match.MatchAcceptResponse
import com.ongo.signal.data.model.match.MatchHistoryResponse
import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface MatchApi {
    @Headers(
        "Content-Type: application/json",
        "accesstoken: asda13"
    )
    @GET("v1/main/gogo")
    suspend fun getMainPost(id: Int): Response<Int>

    @POST("location")
    suspend fun postMatchRegistration(
        @Body request: MatchRegistrationRequest
    ): Response<MatchRegistrationResponse>

    @GET("match-test")
    suspend fun getMatchPossibleUser(
        @Query("locationId") locationId: Long,
    ): Response<List<MatchPossibleResponse>>

    @POST("match/propose")
    suspend fun postProposeMatch(
        @Query("fromId") fromId: Long,
        @Query("toId") toId: Long,
    ): Response<MatchProposeResponse>

    @POST("match/accept")
    suspend fun postProposeAccept(
        @Query("fromId") fromId: Long,
        @Query("toId") toId: Long,
        @Query("flag") flag: Int,
    ): Response<MatchAcceptResponse>

    @POST("call/propose")
    suspend fun postProposeVideoCall(
        @Query("fromId") fromId: Long,
        @Query("toId") toId: Long,
    ): Response<MatchProposeResponse>

    @POST("call/accept")
    suspend fun postVideoCallAccept(
        @Query("fromId") fromId: Long,
        @Query("toId") toId: Long,
        @Query("flag") flag: Int,
    ): Response<MatchAcceptResponse>

    @GET("match/history")
    suspend fun getMatchHistory(
        @Query("userId") userId: Long,
    ): Response<List<MatchHistoryResponse>>

    @DELETE("location")
    suspend fun deleteMatching(
        @Query("locationId") locationId: Long
    ): Response<Void>
}