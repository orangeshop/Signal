package com.ongo.signal.ui.my

import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.data.model.main.BoardDTO
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

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("MyPageViewModel 예외처리 ${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun sendLogout(
        token: String,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (loginRepository.deleteUser(token = "Bearer $token") == 1) {
                dataStoreClass.clearData()
                onSuccess(1)
            }
        }
    }

    fun getMySignal(
        userId: Long,
        onSuccess: (List<BoardDTO>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val response = myPageRepository.getMySignal(userId)
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                    Timber.d(response.body().toString())
                } else {
                    onError(Throwable("Failed to get signals"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun getMyCommentSignal(
        userId: Long,
        onSuccess: (List<BoardDTO>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val response = myPageRepository.getMyCommentSignal(userId)
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError(Throwable("Failed to get comment signals"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}