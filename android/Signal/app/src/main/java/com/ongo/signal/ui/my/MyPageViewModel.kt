package com.ongo.signal.ui.my

import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.my.MyProfileData
import com.ongo.signal.data.repository.login.LoginRepository
import com.ongo.signal.data.repository.mypage.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val myPageRepository: MyPageRepository,
    private val dataStoreClass: DataStoreClass,
) : ViewModel() {

    lateinit var userData: MyProfileData

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("MyPageViewModel 예외처리 ${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun sendLogout(
        accessToken: String,
        refreshToken: String,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            UserSession.userId?.let { userId ->
                loginRepository.postFCMToken(userId, "").onSuccess {
                    if (loginRepository.deleteUser(
                            accessToken = "Bearer $accessToken",
                            refreshToken = "Bearer $refreshToken"
                        ) == 1
                    ) {
                        Timber.d("logout")
                        dataStoreClass.clearData()
                        onSuccess(1)
                    }
                }
            }
        }
    }

    fun getMySignal(
        userId: Long,
        onSuccess: (List<BoardDTO>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                myPageRepository.getMySignal(userId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                    Timber.d(response.body().toString())
                } else {
                    onError(Throwable("Failed to get signals"))
                }
            }.onFailure { exception ->
                onError(exception)
            }
        }
    }

    fun getMyCommentSignal(
        userId: Long,
        onSuccess: (List<BoardDTO>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                myPageRepository.getMyCommentSignal(userId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                    Timber.d(response.body().toString())
                } else {
                    onError(Throwable("Failed to get comment signals"))
                }
            }.onFailure { exception ->
                onError(exception)
            }
        }
    }

    fun getMyProfile(
        token: String,
        onSuccess: (MyProfileData) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            myPageRepository.getMyProfile("Bearer $token").onSuccess { myProfileResponse ->
                myProfileResponse?.let {
                    userData = myProfileResponse.myProfileData
                    onSuccess(userData)
                }
            }
        }
    }

}