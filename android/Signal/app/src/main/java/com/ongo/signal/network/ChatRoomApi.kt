package com.ongo.signal.network


import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeCreate
import com.ongo.signal.data.model.chat.ChatHomeDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatRoomApi {
    @GET("/chat-room")
    suspend fun getChatRoomList(@Query("user_id") user_id: Int): Response<MutableList<ChatHomeDTO>>

    @POST("/chat-room/create")
    suspend fun saveChatRoom(@Body chatRoom: ChatHomeCreate) : Response<ChatHomeCreate>

    @GET("/chat-room/messages")
    suspend fun getAllChatDetail(@Path("user_id") id: Int): Response<MutableList<ChatHomeChildDto>>
}
