package com.ongo.signal.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import com.ongo.signal.data.repository.SignalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val signalRepository: SignalRepository
) : ViewModel() {
    // uiState

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}")
        }

    fun postMatchRegistration(
        request: MatchRegistrationRequest,
        onSuccess: (MatchRegistrationResponse) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            signalRepository.postMatchRegistration(request).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }.onFailure { throw it }
        }
    }

    fun getMatchPossibleUser(
        locationId: Long,
        onSuccess: (List<MatchPossibleResponse>) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            signalRepository.getMatchPossibleUser(locationId).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }.onFailure { throw it }
        }
    }

    fun deleteMatchRegistration(userId: Long) {
        viewModelScope.launch {
            signalRepository.deleteMatchRegistration(userId)
        }
    }


    companion object {
        const val FAILURE_MESSAGE = "통신이 되지 않습니다."
    }
}