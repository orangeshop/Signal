package com.ongo.signal.data.repository.chat.chatservice

import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import retrofit2.Response

interface ChatRepository {
    suspend fun getChatList(): Response<MutableList<ChatHomeDTO>>

    suspend fun saveChatRoom(chatRoom: ChatHomeCreateDTO) : Response<ChatHomeCreateDTO>

    suspend fun getAllMessages(chat_id: Long): Response<MutableList<ChatHomeChildDto>>

    suspend fun readMessage(chat_id: Long)
}