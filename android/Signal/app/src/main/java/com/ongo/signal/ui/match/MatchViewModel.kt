package com.ongo.signal.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import com.ongo.signal.data.repository.match.SignalRepository
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
    // 내유저 정보, 매칭 가능 유저 정보

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
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
            }.onFailure {
                Timber.d("통신 안돼요 ${it.message}")
                throw it
            }
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

    fun postProposeMatch(
        fromId: Long,
        toId: Long,
        onSuccess: (MatchProposeResponse) -> Unit,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            signalRepository.postProposeMatch(fromId, toId).onSuccess { response ->
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