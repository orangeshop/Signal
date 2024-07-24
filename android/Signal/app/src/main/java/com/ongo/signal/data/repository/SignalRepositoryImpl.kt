package com.ongo.signal.data.repository

import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import com.ongo.signal.network.SignalApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRepositoryImpl @Inject constructor(
    private val signalApi: SignalApi
) : SignalRepository {
    override suspend fun getPost(id: Int): Response<Int> {
        return signalApi.getMainPost(id)
    }

    override suspend fun postMatchRegistration(
        request: MatchRegistrationRequest
    ): Response<MatchRegistrationResponse> {
        return signalApi.postMatchRegistration(request)
    }

    override suspend fun deleteMatchRegistration(userId: Long): Response<Int> {
        return signalApi.deleteMatchRegistration(userId)
    }

}