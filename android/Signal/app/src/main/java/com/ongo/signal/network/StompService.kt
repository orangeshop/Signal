package com.ongo.signal.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompService @Inject constructor(
    authInterceptor: AuthInterceptor
) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            authInterceptor
        ).build()

    private val client = StompClient(
        OkHttpWebSocketClient(okHttpClient)
    )

    suspend fun connect(url: String): StompSession {
        return client.connect(url)
    }
}