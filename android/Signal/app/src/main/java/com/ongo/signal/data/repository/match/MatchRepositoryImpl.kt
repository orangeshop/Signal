package com.ongo.signal.data.repository.match

import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import com.ongo.signal.network.MatchApi
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val signalApi: MatchApi
) : MatchRepository {
    override suspend fun getPost(id: Int): Response<Int> {
        return signalApi.getMainPost(id)
    }

    override suspend fun postMatchRegistration(
        request: MatchRegistrationRequest
    ): Result<MatchRegistrationResponse?> {
        val req = signalApi.postMatchRegistration(request)
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun deleteMatchRegistration(userId: Long): Response<Int> {
        return signalApi.deleteMatchRegistration(userId)
    }

    override suspend fun getMatchPossibleUser(locationId: Long): Result<List<MatchPossibleResponse>?> {
        val req = signalApi.getMatchPossibleUser(locationId)
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postProposeMatch(
        fromId: Long,
        toId: Long,
    ): Result<MatchProposeResponse?> {

        val req = signalApi.postProposeMatch(fromId,toId)
        Timber.d("${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }
}