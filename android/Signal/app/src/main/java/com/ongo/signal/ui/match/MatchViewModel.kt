package com.ongo.signal.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.match.MatchAcceptResponse
import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import com.ongo.signal.data.repository.match.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {
    // uiState 
    // 내유저 정보, 매칭 가능 유저 정보
    private var _otherUserId: Long? = null
    val otherUserId: Long?
        get() = _otherUserId

    private var _otherUserName: String? = null
    val otherUserName: String?
        get() = _otherUserName

    private var _selectType: String? = null
    val selectType: String?
        get() = _selectType

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun postMatchRegistration(
        request: MatchRegistrationRequest,
        onSuccess: (MatchRegistrationResponse) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postMatchRegistration(request).onSuccess { response ->
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
            matchRepository.getMatchPossibleUser(locationId).onSuccess { response ->
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
            matchRepository.postProposeMatch(fromId, toId).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }.onFailure { throw it }
        }
    }

    fun postProposeAccept(
        fromId: Long,
        toId: Long,
        flag: Int,
        onSuccess: (MatchAcceptResponse) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postProposeAccept(fromId, toId, flag).onSuccess { response ->
                response?.let {
                    onSuccess(response)
                }
            }
        }
    }

    fun postProposeVideoCall(
        fromId: Long,
        toId: Long,
        onSuccess: (MatchProposeResponse) -> Unit,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postProposeVideoCall(24, 25).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }.onFailure { throw it }
        }
    }

    fun postProposeVideoCallAccept(
        fromId: Long,
        toId: Long,
        flag: Int,
        onSuccess: (MatchAcceptResponse) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postProposeVideoCallAccept(25, 24, 1).onSuccess { response ->
                response?.let {
                    onSuccess(response)
                }
            }
        }
    }

    fun deleteMatchRegistration(userId: Long) {
        viewModelScope.launch {
            matchRepository.deleteMatchRegistration(userId)
        }
    }

    fun setOtherUserId(userId: Long) {
        _otherUserId = userId
    }

    fun setOtherUserName(userName: String) {
        _otherUserName = userName
    }

    fun setMemberType(selectType: String) {
        _selectType = selectType
    }


    companion object {
        const val FAILURE_MESSAGE = "통신이 되지 않습니다."
    }
}