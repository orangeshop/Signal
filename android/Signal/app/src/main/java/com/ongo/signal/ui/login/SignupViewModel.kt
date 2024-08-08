package com.ongo.signal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.login.SignupRequest
import com.ongo.signal.data.model.login.SignupUIState
import com.ongo.signal.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreClass: DataStoreClass,
) : ViewModel() {

    var uiState =
        SignupUIState(userId = "", userName = "", password = "", passwordCheck = "")

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
        }


    fun setUserId(userId: String) {
        uiState = uiState.copy(userId = userId)
    }

    fun setPassword(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun setPasswordCheck(passwordCheck: String) {
        uiState = uiState.copy(passwordCheck = passwordCheck)
    }

    fun setName(userName: String) {
        uiState = uiState.copy(userName = userName)
    }

    fun setType(type: String) {
        uiState = uiState.copy(type = type)
    }

    fun setPossible(isPossible: Boolean?) {
        uiState = uiState.copy(isPossibleId = isPossible)
    }

    fun setImageFile(imageFile: MultipartBody.Part) {
        uiState = uiState.copy(imageFile = imageFile)
    }

    fun postSignup(onSuccess: (Long) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userRepository.postSignup(
                request = SignupRequest(
                    loginId = uiState.userId,
                    name = uiState.userName,
                    password = uiState.password,
                    type = uiState.type
                )
            ).onSuccess { response ->
                response?.let {
                    UserSession.userId = it.userInfo.userId
                    UserSession.userName = it.userInfo.name
                    UserSession.accessToken = it.accessToken
                    UserSession.refreshToken = it.refreshToken

                    saveUserData(
                        userId = UserSession.userId!!,
                        userName = UserSession.userName!!,
                        accessToken = UserSession.accessToken!!,
                        refreshToken = UserSession.refreshToken!!,
                    )

                    onSuccess(it.userInfo.userId)
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


    fun checkDuplicatedId(userId: String, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            userRepository.postCheckPossibleId(userId).onSuccess { response ->
                response?.let {
                    Timber.d("중복확인3 ${response.duplicated}")
                    uiState = uiState.copy(isPossibleId = !response.duplicated)
                    onSuccess(!response.duplicated)
                }
            }
        }
    }

    fun checkUIState(): Pair<Boolean, String> {
        with(uiState) {
            if (isPossibleId == null) return Pair(false, "아이디 중복 확인을 해주세요")
            if (isPossibleId == false) return Pair(false, "사용 불가능한 아이디입니다.")
            if (password.isBlank()) return Pair(false, "비밀번호를 입력해주세요")
            if (checkPasswordStandard(password) == false) return Pair(
                false,
                "비밀번호는 문자, 특수문자. 숫자를 포함하여 8자 이상 입력해주세요."
            )
            if (password != passwordCheck) return Pair(false, "비밀번호와 비밀번호 확인이 다릅니다")
            if (userName.isBlank()) return Pair(false, "이름을 입력해주세요")

            return Pair(true, "${uiState}")
        }

    }

    private fun checkPasswordStandard(password: String): Boolean {
        // 최소 8자, 하나 이상의 문자, 하나 이상의 숫자, 하나 이상의 특수문자 포함 여부 확인
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return passwordMatcher.matches(password)
    }

    private fun saveUserData(
        userId: Long,
        userName: String,
        profileImage: String = "",
        accessToken: String,
        refreshToken: String,
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