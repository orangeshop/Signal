package com.ongo.signal.data.repository.chat.chatservice

import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.my.MyProfileResponse
import com.ongo.signal.data.model.review.UserProfileResponse
import retrofit2.Response

interface ChatRepository {
    suspend fun getChatList(): Response<MutableList<ChatHomeDTO>>

    suspend fun saveChatRoom(chatRoom: ChatHomeCreateDTO) : Response<ChatHomeCreateDTO>

    suspend fun getAllMessages(chat_id: Long): Response<MutableList<ChatHomeChildDTO>>

    suspend fun readMessage(chat_id: Long, userId : Long)

    suspend fun getUserProfile(chat_id: Long): Response<UserProfileResponse>

    suspend fun deleteChatRoom(chat_id: Long)
}