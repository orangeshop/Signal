package com.ongo.signal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.SignalUser
import com.ongo.signal.data.repository.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val dataStoreClass: DataStoreClass,
) : ViewModel() {

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun postLoginRequest(
        request: LoginRequest,
        onSuccess: (Boolean, SignalUser?) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.postLogin(request).onSuccess { response ->
                response?.let {
                    if (response.status) {
                        onSuccess(
                            true,
                            SignalUser(
                                loginId = it.userInfo.loginId,
                                accessToken = it.accessToken,
                                accessTokenExpireTime = it.accessTokenExpireTime,
                                type = it.userInfo.type,
                                userId = it.userInfo.userId,
                                userName = it.userInfo.name,
                                refreshToken = it.refreshToken,
                                refreshTokenExpireTime = it.refreshTokenExpireTime
                            )
                        )
                    } else {
                        onSuccess(
                            false,
                            null
                        )
                    }

                }
            }
        }
    }

    fun saveUserData(
        userId: Long,
        userName: String,
        profileImage: String = "",
        accessToken: String,
        refreshToken: String
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            dataStoreClass.setIsLogin(true)
            dataStoreClass.setUserId(userId)
            dataStoreClass.setUserName(userName)
            dataStoreClass.setProfileImage(profileImage)
            dataStoreClass.setAccessToken(accessToken)
            dataStoreClass.setRefreshToken(refreshToken)
        }
    }
}