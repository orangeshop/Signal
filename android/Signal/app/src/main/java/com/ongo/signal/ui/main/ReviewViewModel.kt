package com.ongo.signal.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.match.MatchHistoryResponse
import com.ongo.signal.data.repository.match.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    fun checkReviewPermission(userId: Long, onResult: (Boolean) -> Unit) {
        getMatchHistory(userId) { historyList ->
            val matchList = historyList.filter {
                (it.proposeId == UserSession.userId && it.acceptId == userId)
                        || (it.proposeId == userId && it.acceptId == UserSession.userId)
            }
            Timber.d("$matchList")
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


}