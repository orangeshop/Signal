package com.ongo.signal.config

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val TAG = "CreateChatRoom_싸피"

interface ChatRoomApi {
    @POST("/chat-room/create")
    suspend fun saveChatRoom(@Body chatRoom: ChatHomeCreateDTO) : Response<ChatHomeCreateDTO>
}


object CreateChatRoom {

    // Gson 인스턴스를 제공하는 객체
    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    // Retrofit 인스턴스를 제공하는 객체
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.100.95:8080/") // API 서버의 기본 URL
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ChatRoomApi 인스턴스를 제공하는 객체
    private val chatRoomApi: ChatRoomApi by lazy {
        retrofit.create(ChatRoomApi::class.java)
    }

    fun Create(from : Long, to : Long){
        CoroutineScope(Dispatchers.IO).launch {
            chatRoomApi.saveChatRoom(ChatHomeCreateDTO(from, to))
            Log.d(TAG, "Create: 완료")
        }
    }
}



