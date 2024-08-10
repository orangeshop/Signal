package com.ongo.signal.network


import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.login.LoginUserResponse
import com.ongo.signal.data.model.my.ProfileEditRequest
import com.ongo.signal.data.model.review.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatRoomApi {
    @GET("/chat-room")
    suspend fun getChatRoomList(@Query("user_id") user_id: Long): Response<MutableList<ChatHomeDTO>>

    @POST("/chat-room/create")
    suspend fun saveChatRoom(@Body chatRoom: ChatHomeCreateDTO) : Response<ChatHomeCreateDTO>

    @GET("/chat-room/messages")
    suspend fun getAllChatDetail(@Query("chat_id") chat_id: Long): Response<MutableList<ChatHomeChildDTO>>

    @PATCH("/message/read")
    suspend fun readMessage(@Query("chat_id") chat_id: Long)

    @GET("user/{id}")
    suspend fun getUserProfile(@Path("id") id: Long): Response<UserProfileResponse>
}
