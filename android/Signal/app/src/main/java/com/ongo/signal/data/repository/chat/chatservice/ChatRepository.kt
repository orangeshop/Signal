package com.ongo.signal.data.repository.chat.chatservice

import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeCreate
import com.ongo.signal.data.model.chat.ChatHomeDTO
import retrofit2.Response

interface ChatRepository {
    suspend fun getChatList(): Response<MutableList<ChatHomeDTO>>

    suspend fun saveChatRoom(chatRoom: ChatHomeCreate) : Response<ChatHomeCreate>

    suspend fun getAllMessages(chat_id: Int): Response<MutableList<ChatHomeChildDto>>

}