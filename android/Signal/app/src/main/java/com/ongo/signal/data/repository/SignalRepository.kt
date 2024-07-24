package com.ongo.signal.data.repository

import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import retrofit2.Response

interface SignalRepository {

    suspend fun getPost(id: Int): Response<Int>

    suspend fun postMatchRegistration(
        request: MatchRegistrationRequest
    ): Response<MatchRegistrationResponse>

    suspend fun deleteMatchRegistration(userId: Long): Response<Int>
}