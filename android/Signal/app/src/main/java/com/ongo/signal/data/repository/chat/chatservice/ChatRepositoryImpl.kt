package com.ongo.signal.data.repository.chat.chatservice

import android.util.Log
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.my.MyProfileResponse
import com.ongo.signal.data.model.review.UserProfileResponse
import com.ongo.signal.network.ChatRoomApi
import com.ongo.signal.network.MyPageApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ChatRepositoryImpl_싸피"
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatRoomApi,
): ChatRepository {
    override suspend fun getChatList(): Response<MutableList<ChatHomeDTO>> {
        return chatApi.getChatRoomList(UserSession.userId ?: 0)
    }

    override suspend fun saveChatRoom(chatRoom: ChatHomeCreateDTO): Response<ChatHomeCreateDTO> {
        return chatApi.saveChatRoom(chatRoom)
    }

    override suspend fun getAllMessages(chat_id: Long): Response<MutableList<ChatHomeChildDTO>> {
        return chatApi.getAllChatDetail(chat_id)
    }

    override suspend fun readMessage(chat_id: Long, userId : Long){
        return chatApi.readMessage(chat_id, userId)
    }

    override suspend fun getUserProfile(chat_id: Long): Response<UserProfileResponse> {
        return chatApi.getUserProfile(chat_id)
    }

    override suspend fun deleteChatRoom(chat_id: Long) {
        chatApi.deleteChatRoom(chat_id)
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