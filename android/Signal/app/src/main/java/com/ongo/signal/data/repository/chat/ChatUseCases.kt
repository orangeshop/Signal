package com.ongo.signal.data.repository.chat

import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO

interface ChatUseCases {
    fun clearMessageList()
    suspend fun loadChats(): List<ChatHomeDTO>
    suspend fun saveChat(room: ChatHomeDTO)
    suspend fun loadDetailList(id: Int): List<ChatHomeChildDto>
    suspend fun saveDetailList(message: ChatHomeChildDto, id: Int)
    fun timeSetting(): String
    suspend fun stompSend(item: ChatHomeChildDto)
    suspend fun stompGet(chatRoomNumber: Int)
    suspend fun connectedWebSocket(chatRoomNumber: Int)
    suspend fun stompDisconnect()
}