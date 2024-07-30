package com.ongo.signal.data.repository.chat.chatservice

import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeCreate
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.network.ChatRoomApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatRoomApi
): ChatRepository {
    override suspend fun getChatList(): Response<MutableList<ChatHomeDTO>> {
        return chatApi.getChatRoomList(8)
    }

    override suspend fun saveChatRoom(chatRoom: ChatHomeCreate): Response<ChatHomeCreate> {
        return chatApi.saveChatRoom(ChatHomeCreate(5, 8))
    }

    override suspend fun getAllMessages(chat_id: Int): Response<MutableList<ChatHomeChildDto>> {
        return chatApi.getAllChatDetail(chat_id)
    }
}