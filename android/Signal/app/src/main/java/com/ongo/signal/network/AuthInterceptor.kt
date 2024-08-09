package com.ongo.signal.network

import com.ongo.signal.config.UserSession
import com.ongo.signal.data.repository.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = UserSession.accessToken

        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            synchronized(this) {
                // 새로운 토큰 받아와서 갈아끼워 주기
                UserSession.refreshToken?.let { refreshToken ->
                    val newAccessToken = runBlocking {
                        authRepository.renewalToken(refreshToken = refreshToken).onSuccess {
                            it?.let { response ->
                                response.accessToken
                            } ?: ""
                        }.onFailure {
                            ""
                        }
                    }
                    Timber.d("인터셉터에서 새로받은 token ${newAccessToken}")
                    //
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${newAccessToken}")
                        .build()

                    return chain.proceed(newRequest)

                }

            }
        }

        return response
    }
}
