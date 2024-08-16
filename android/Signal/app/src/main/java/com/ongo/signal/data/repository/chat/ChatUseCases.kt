package com.ongo.signal.data.repository.chat

import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.data.model.review.UserProfileResponse

interface ChatUseCases {
    suspend fun loadChats(id: Long): List<ChatHomeDTO>
    suspend fun saveChat(room: ChatHomeDTO)
    suspend fun loadDetailList(id: Long, loading: Long): List<ChatHomeChildDTO>
    suspend fun saveDetailList(message: ChatHomeChildDTO, id: Long)
    suspend fun readMessage(id: Long, userId : Long)
    fun timeSetting(): String

    suspend fun deleteChatRoom(id : Long)

    suspend fun getUserProfile(id: Long) : UserProfileResponse

    suspend fun deleteChat(id: Long)

    suspend fun loadReadMessage(id: Long) : Int

    suspend fun loadDetailListNetwork(id: Long) : List<ChatHomeChildDTO>
    suspend fun loadDetailListNoId(id: Long) : List<ChatHomeChildDTO>

    suspend fun createChatRoom(chatRoom: ChatHomeCreateDTO)

    suspend fun stompSend(item: ChatHomeChildDTO, onSuccess: () -> Unit)
    suspend fun stompGet(chatRoomNumber: Long, onSuccess: (Long) -> Unit)
    suspend fun connectedWebSocket(chatRoomNumber: Long)
    suspend fun stompDisconnect()


    suspend fun saveLocalMessage(room: ChatHomeLocalCheckDTO)
    suspend fun getLastMessageIndex(chatId: Long): ChatHomeLocalCheckDTO?
    fun updateLastReadMessageIndex(chatId: Long, lastReadMessageIndex: Long, sendAt: String)
    fun updateTodayFirstMessage(chatId: Long, todayFirstSendMessageId: Long, sendAt: String)
    fun updateMessageAmount(ChatId: Long, messageVolume: Long, sendAt: String)
}