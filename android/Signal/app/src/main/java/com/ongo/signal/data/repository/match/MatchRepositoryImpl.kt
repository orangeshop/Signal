package com.ongo.signal.data.repository.match

import com.ongo.signal.data.model.match.MatchAcceptResponse
import com.ongo.signal.data.model.match.MatchHistoryResponse
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
    private val matchApi: MatchApi
) : MatchRepository {
    override suspend fun getPost(id: Int): Response<Int> {
        return matchApi.getMainPost(id)
    }

    override suspend fun postMatchRegistration(
        request: MatchRegistrationRequest
    ): Result<MatchRegistrationResponse?> {
        val req = matchApi.postMatchRegistration(request)
        Timber.d("매칭 등록 $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun getMatchPossibleUser(locationId: Long): Result<List<MatchPossibleResponse>?> {
        val req = matchApi.getMatchPossibleUser(locationId)
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

        val req = matchApi.postProposeMatch(fromId, toId)
        Timber.d("postProposeMatch ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postProposeAccept(
        fromId: Long,
        toId: Long,
        flag: Int
    ): Result<MatchAcceptResponse?> {
        val req = matchApi.postProposeAccept(fromId, toId, flag)
        Timber.d("postProposeAccept ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postProposeVideoCall(
        fromId: Long,
        toId: Long
    ): Result<MatchProposeResponse?> {
        val req = matchApi.postProposeVideoCall(fromId, toId)
        Timber.d("영통 응답 확인 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postProposeVideoCallAccept(
        fromId: Long,
        toId: Long,
        flag: Int
    ): Result<MatchAcceptResponse?> {
        val req = matchApi.postVideoCallAccept(fromId, toId, flag)
        Timber.d("영통 수락 확인 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun getMatchHistory(userId: Long): Result<List<MatchHistoryResponse>?> {
        val req = matchApi.getMatchHistory(userId)
        Timber.d("매칭 이력 확인 $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun deleteMatching(locationId: Long): Result<Boolean> {
        val req = matchApi.deleteMatching(locationId)
        Timber.d("매칭 삭제 확인${req}")
        return if (req.isSuccessful) {
            Result.success(true)
        } else {
            Result.failure(Exception())
        }
    }
}