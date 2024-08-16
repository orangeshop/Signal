package com.ongo.signal.data.repository.review

import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.data.model.review.ReviewResponseDTO
import com.ongo.signal.data.model.review.ReviewResponseItemDTO
import com.ongo.signal.data.model.review.UserProfileResponse
import com.ongo.signal.network.ReviewApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(private val reviewApi: ReviewApi) :
    ReviewRepository {
    override suspend fun writeReview(reviewRequestDTO: ReviewRequestDTO): Response<ReviewResponseItemDTO> {
        return reviewApi.writeReview(reviewRequestDTO)
    }

    override suspend fun getReview(userId: Long): Response<ReviewResponseDTO> {
        return reviewApi.getReview(userId)
    }

    override suspend fun getUserProfile(userId: Long): Result<UserProfileResponse?> {
        val req = reviewApi.getUserProfile(userId)

        if (req.isSuccessful) {
            return Result.success(req.body())
        } else {
            return Result.failure(Exception())
        }
    }


}