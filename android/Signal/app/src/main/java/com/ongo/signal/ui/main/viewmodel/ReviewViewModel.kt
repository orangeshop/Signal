package com.ongo.signal.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.match.MatchHistoryResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.data.model.review.ReviewResponseItemDTO
import com.ongo.signal.data.model.review.UserProfileResponse
import com.ongo.signal.data.repository.match.MatchRepository
import com.ongo.signal.data.repository.review.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _reviewList = MutableStateFlow<List<ReviewResponseItemDTO>>(emptyList())
    val reviewList: StateFlow<List<ReviewResponseItemDTO>> = _reviewList

    private val _isReviewVisible = MutableStateFlow(false)
    val isReviewVisible: StateFlow<Boolean> = _isReviewVisible

    private val _canWriteReview = MutableStateFlow(false)
    val canWriteReview: StateFlow<Boolean> = _canWriteReview

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun checkReviewPermission(userId: Long) {
        getMatchHistory(userId) { historyList ->
            val matchList = historyList.filter {
                (it.proposeId == UserSession.userId && it.acceptId == userId)
                        || (it.proposeId == userId && it.acceptId == UserSession.userId)
            }
            Timber.d("MatchList for userId $userId: $matchList")
            _isReviewVisible.value = matchList.isNotEmpty()
            Timber.d("isReviewVisible: ${_isReviewVisible.value}")
            _isReviewVisible.value = matchList.isNotEmpty()
            if (_isReviewVisible.value) {
                loadReview(userId)
            }
        }
    }

    private fun getMatchHistory(
        userId: Long,
        onSuccess: (List<MatchHistoryResponse>) -> Unit
    ) {
        viewModelScope.launch {
            matchRepository.getMatchHistory(userId).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }
        }
    }

    fun loadReview(userId: Long) {
        viewModelScope.launch {
            runCatching {
                reviewRepository.getReview(userId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val newReviewList = response.body() ?: emptyList()
                    _reviewList.value = newReviewList
                    Timber.d(newReviewList.toString())
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("Failed to load review: $errorMessage")
                }
                updateCanWriteReview(userId)
            }.onFailure { e ->
                Timber.e(e, "Failed to load review")
                updateCanWriteReview(userId)
            }
        }
    }

    fun writeReview(reviewRequestDTO: ReviewRequestDTO) {
        viewModelScope.launch {
            runCatching {
                reviewRepository.writeReview(reviewRequestDTO)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    Timber.d("write review is successful: ${response.body()}")
                } else {
                    Timber.e("Failed to write review: ${response.errorBody()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to write review")
            }
        }
    }

    fun postProposeMatch(
        fromId: Long,
        toId: Long,
        onSuccess: (MatchProposeResponse) -> Unit,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postProposeMatch(fromId, toId).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }.onFailure { throw it }
        }
    }

    fun getUserProfile(
        userId: Long,
        onSuccess: (UserProfileResponse) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            reviewRepository.getUserProfile(userId).onSuccess { userProfileResponse ->
                userProfileResponse?.let {
                    onSuccess(userProfileResponse)
                }
            }
        }
    }

    private fun updateCanWriteReview(userId: Long) {
        val hasPermission = _isReviewVisible.value
        Timber.d("Review Visibility Permission (hasPermission): $hasPermission")

        val hasWrittenReview = _reviewList.value.any {
            it.writerId == UserSession.userId && it.userId == userId
        }
        Timber.d("Has Written Review (hasWrittenReview) permission: $hasWrittenReview for userId: $userId")

        _canWriteReview.value = hasPermission && !hasWrittenReview
        Timber.d("permission Can Write Review (canWriteReview): ${canWriteReview.value} for userId: $userId")
    }
}