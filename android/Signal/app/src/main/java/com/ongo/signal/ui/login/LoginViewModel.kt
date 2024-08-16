package com.ongo.signal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.data.model.login.SignalUser
import com.ongo.signal.data.repository.auth.AuthRepository
import com.ongo.signal.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreClass: DataStoreClass,
    private val authRepository: AuthRepository,
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

    fun autoLogin(onLogin: (SignalUser?, String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("자동 로그인 시작")
            val isLogin = dataStoreClass.isLoginData.first()
            Timber.d("자동 로그인 여부: $isLogin")
            if (isLogin) {
                try {
                    val userLoginId = dataStoreClass.userLoginIdData.first()
                    val userPassword = dataStoreClass.userEncodePasswordData.first()
                    Timber.d("자동 로그인 - 아이디: $userLoginId, 비밀번호 길이: ${userPassword.length}")

                    postAutoLoginRequest(
                        request = LoginRequest(
                            loginId = userLoginId,
                            password = userPassword
                        ),
                        onSuccess = { isSuccess, signalUser ->
                            Timber.d("자동 로그인 결과 - 성공 여부: $isSuccess, 사용자: ${signalUser?.userId}")
                            onLogin(signalUser, userLoginId, userPassword)
                        }
                    )
                } catch (e: Exception) {
                    Timber.e("자동 로그인 중 예외 발생: ${e.message}")
                    onLogin(null, "", "")
                }
            } else {
                Timber.d("자동 로그인이 설정되지 않음")
                onLogin(null, "", "")
            }
        }
    }

    private fun postAutoLoginRequest(
        request: LoginRequest,
        onSuccess: (Boolean, SignalUser?) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            authRepository.autoLogin(request).onSuccess { response ->
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
                                refreshTokenExpireTime = it.refreshTokenExpireTime,
                                userEncodePassword = it.userInfo.password
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

    fun postLoginRequest(
        request: LoginRequest,
        onSuccess: (Boolean, SignalUser?) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            authRepository.postLogin(request).onSuccess { response ->
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
                                refreshTokenExpireTime = it.refreshTokenExpireTime,
                                userEncodePassword = it.userInfo.password
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

    fun loginWithNaver(token: String, onResult: (Boolean, LoginResponse?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.naverLogin(token)
            result.fold(
                onSuccess = {
                    Timber.d("네이버 로그인 성공: $it")
                    onResult(true, it)
                },
                onFailure = {
                    Timber.e(it, "네이버 로그인 실패")
                    onResult(false, null)
                }
            )
        }
    }

    fun saveUserData(
        userId: Long,
        userLoginId: String,
        userName: String,
        userPassword: String,
        profileImage: String = "",
        accessToken: String,
        refreshToken: String,
        userEncodePassword: String
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
            dataStoreClass.setUserEncodePassword(userEncodePassword)
        }
    }

    fun handleKakaoLogin(accessToken: String, onResult: (Boolean, LoginResponse?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.kakaoLogin(accessToken)
            result.fold(
                onSuccess = { loginResponse ->
                    Timber.d("카카오 로그인 성공: $loginResponse")
                    onResult(true, loginResponse)
                },
                onFailure = { error ->
                    Timber.e(error, "카카오 로그인 실패")
                    onResult(false, null)
                }
            )
        }
    }
}