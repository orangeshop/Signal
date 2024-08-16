package com.ongo.signal.ui.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.my.MyProfileData
import com.ongo.signal.data.repository.auth.AuthRepository
import com.ongo.signal.data.repository.user.UserRepository
import com.ongo.signal.data.repository.mypage.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val myPageRepository: MyPageRepository,
    private val dataStoreClass: DataStoreClass,
    private val authRepository: AuthRepository,
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
                userRepository.postFCMToken(userId, "").onSuccess {
                    if (authRepository.deleteUser(
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
        onSuccess: (MyProfileData) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            myPageRepository.getMyProfile().onSuccess { myProfileResponse ->
                myProfileResponse?.let {
                    userData = myProfileResponse.myProfileData
                    onSuccess(userData)
                }
            }
        }
    }
}
