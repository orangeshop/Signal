package com.ongo.signal.ui.login

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ongo.signal.config.UserSession
import timber.log.Timber

object NaverLoginCallback : OAuthLoginCallback {

    private var onSuccess: ((String, String) -> Unit)? = null
    private var onFailure: ((String) -> Unit)? = null

    override fun onError(errorCode: Int, message: String) {
        onFailure?.invoke("네이버 로그인 오류: $message ($errorCode)")
    }

    override fun onFailure(httpStatus: Int, message: String) {
        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
        Timber.e("네이버 로그인 실패: $errorDescription ($errorCode)")
        onFailure?.invoke("네이버 로그인 실패: $errorDescription ($errorCode)")
    }

    override fun onSuccess() {
        val accessToken = NaverIdLoginSDK.getAccessToken() ?: return
        val refreshToken = NaverIdLoginSDK.getRefreshToken() ?: return

        UserSession.accessToken = accessToken
        UserSession.refreshToken = refreshToken
        Timber.d("네이버 로그인 성공: AccessToken = $accessToken, RefreshToken = $refreshToken")
        onSuccess?.invoke(accessToken, refreshToken)
    }

    fun setOnSuccessCallback(callback: (String, String) -> Unit) {
        onSuccess = callback
    }

    fun setOnFailureCallback(callback: (String) -> Unit) {
        onFailure = callback
    }

}