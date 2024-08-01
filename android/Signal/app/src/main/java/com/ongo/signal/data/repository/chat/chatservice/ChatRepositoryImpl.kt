package com.ongo.signal.data.repository.chat.chatservice

import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
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
        return chatApi.getChatRoomList(UserSession.userId ?: 0)
    }

    override suspend fun saveChatRoom(chatRoom: ChatHomeCreateDTO): Response<ChatHomeCreateDTO> {
        return chatApi.saveChatRoom(chatRoom)
    }

    override suspend fun getAllMessages(chat_id: Long): Response<MutableList<ChatHomeChildDto>> {
        return chatApi.getAllChatDetail(chat_id)
    }

    override suspend fun readMessage(chat_id: Long){
        return chatApi.readMessage(chat_id)
    }

    companion object {
        private var instance: ChatRepositoryImpl? = null
        fun getInstance(chatApi: ChatRoomApi): ChatRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: ChatRepositoryImpl(chatApi).also { instance = it }
            }
        }
    }
}