package com.ongo.signal.data.repository.chat

import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO

interface ChatUseCases {
    suspend fun loadChats(): List<ChatHomeDTO>
    suspend fun saveChat(room: ChatHomeDTO)
    suspend fun loadDetailList(id: Long): List<ChatHomeChildDto>
    suspend fun saveDetailList(message: ChatHomeChildDto, id: Long)
    fun timeSetting(): String
    suspend fun stompSend(item: ChatHomeChildDto)
    suspend fun stompGet(chatRoomNumber: Long, onSuccess: (Long) -> Unit)
    suspend fun connectedWebSocket(chatRoomNumber: Long)
    suspend fun stompDisconnect()
}