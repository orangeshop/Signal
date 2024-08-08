package com.ongo.signal.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

//class AuthInterceptor @Inject constructor(
//    private val authApi: AuthApi
//) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val accessToken = tokenManager.accessToken
//
//        val request = originalRequest.newBuilder()
//            .header("Authorization", "Bearer $accessToken")
//            .build()
//
//        val response = chain.proceed(request)
//
//        if (response.code == 401) {
//            synchronized(this) {
//                val newAccessToken = tokenManager.accessToken
//                if (accessToken == newAccessToken) {
//                    val refreshToken = tokenManager.refreshToken
//                    val newTokens = authApi.refreshToken(refreshToken!!).execute().body()
//                    tokenManager.accessToken = newTokens?.accessToken
//                    tokenManager.refreshToken = newTokens?.refreshToken
//
//                    // 새로운 AccessToken으로 요청을 재시도
//                    val newRequest = originalRequest.newBuilder()
//                        .header("Authorization", "Bearer ${newTokens?.accessToken}")
//                        .build()
//
//                    return chain.proceed(newRequest)
//                }
//            }
//        }
//
//        return response
//    }
//}
