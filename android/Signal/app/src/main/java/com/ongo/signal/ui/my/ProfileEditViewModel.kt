package com.ongo.signal.ui.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.my.ProfileEditRequest
import com.ongo.signal.data.model.my.ProfileEditUiState
import com.ongo.signal.data.repository.user.UserRepository
import com.ongo.signal.data.repository.mypage.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    lateinit var profileEditUiState: ProfileEditUiState

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("MyPageViewModel 예외처리 ${throwable.message}\n\n${throwable.stackTrace}")
        }

    fun putUserProfile(userId: Long, request: ProfileEditRequest, onSuccess: () -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            myPageRepository.putUserProfile(userId, request).onSuccess {
                onSuccess()
            }
        }
    }

    fun putProfileImage(
        userId: Long,
        imageFile: MultipartBody.Part,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userRepository.putProfileImage(userId, imageFile).onSuccess { response ->
                response?.let {
                    UserSession.profileImage = response.fileUrl
                    onSuccess()
                }
            }
        }
    }

    fun postProfileImage(
        userId: Long,
        imageFile: MultipartBody.Part,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userRepository.postProfileImage(userId, imageFile).onSuccess { response ->
                response?.let {
                    UserSession.profileImage = response.fileUrl
                    onSuccess()
                }
            }
        }
    }

    fun setMyProfileEditUiState(nowState: ProfileEditUiState) {
        profileEditUiState = nowState
    }

    fun setImageFile(imageFile: MultipartBody.Part) {
        profileEditUiState = profileEditUiState.copy(imageFile = imageFile)
    }

    fun setName(name: String) {
        profileEditUiState = profileEditUiState.copy(name = name)
    }

    fun setType(type: String) {
        profileEditUiState = profileEditUiState.copy(type = type)
    }

    fun setComment(comment: String) {
        profileEditUiState = profileEditUiState.copy(comment = comment)
    }

    fun setChanged(isChanged: Boolean) {
        profileEditUiState = profileEditUiState.copy(isProfileImageChanged = isChanged)
    }

    fun setExist(isExist: Boolean) {
        profileEditUiState = profileEditUiState.copy(isProfileImageExisted = isExist)
    }

}