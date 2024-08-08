package com.ongo.signal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.repository.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // fcm 토큰 서버에 쏘기
    fun postFCMToken(FCMToken: String) {
        Timber.d("토큰 왔어요 $FCMToken")
        viewModelScope.launch {
            UserSession.userId?.let {
                userRepository.postFCMToken(it, FCMToken)
                    .onSuccess { FCMResponse ->
                        Timber.d("성공적으로 FCM 토큰을 보냈습니다 $FCMResponse")
                    }
            }
        }
    }
}