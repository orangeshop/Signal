package com.ongo.signal.ui.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.repository.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val loginRepository: LoginRepository
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
            if(loginRepository.deleteUser(token = "Bearer $token") == 1){
                onSuccess(1)
            }
        }
    }


}