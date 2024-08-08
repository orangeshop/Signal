package com.ongo.signal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.SignalUser
import com.ongo.signal.data.repository.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreClass: DataStoreClass,
) : ViewModel() {

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun checkLogin(onLogin: (SignalUser?, String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isLogin = dataStoreClass.isLoginData.first()
            if (isLogin) {
                val userLoginId = dataStoreClass.userLoginIdData.first()
                val userPassword = dataStoreClass.userPasswordData.first()
                Timber.d("로그인 확인 합니다4 아이디는 ${userLoginId} 비밀번호는 ${userPassword} 비번 길이 ${userPassword.length}")

                postLoginRequest(
                    request = LoginRequest(
                        loginId = userLoginId,
                        password = userPassword
                    ),
                    onSuccess = { isSuccess, signalUser ->
                        if (isSuccess) {
                            onLogin(signalUser, userLoginId, userPassword)
                        }
                    }
                )
            }
        }
    }

    fun postLoginRequest(
        request: LoginRequest,
        onSuccess: (Boolean, SignalUser?) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userRepository.postLogin(request).onSuccess { response ->
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
        userLoginId: String,
        userName: String,
        userPassword: String,
        profileImage: String = "",
        accessToken: String,
        refreshToken: String
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            dataStoreClass.setIsLogin(true)
            dataStoreClass.setUserId(userId)
            dataStoreClass.setUserLoginId(userLoginId)
            dataStoreClass.setUserName(userName)
            dataStoreClass.setUserPassword(userPassword)
            dataStoreClass.setProfileImage(profileImage)
            dataStoreClass.setAccessToken(accessToken)
            dataStoreClass.setRefreshToken(refreshToken)
        }
    }
}