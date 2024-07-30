package com.ongo.signal.data.repository.match

import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import retrofit2.Response

interface MatchRepository {

    suspend fun getPost(id: Int): Response<Int>

    suspend fun postMatchRegistration(
        request: MatchRegistrationRequest
    ): Result<MatchRegistrationResponse?>

    suspend fun deleteMatchRegistration(userId: Long): Response<Int>

    suspend fun getMatchPossibleUser(locationId: Long): Result<List<MatchPossibleResponse>?>

    suspend fun postProposeMatch(
        fromId: Long,
        toId: Long,
    ): Result<MatchProposeResponse?>

}