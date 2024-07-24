package com.ongo.signal.data.repository

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
        latitude: Double,
        longitude: Double,
        user_id: Long
    ): Response<Int> {
        return signalApi.postMatchRegistration(latitude, longitude, user_id)
    }
}