package com.ongo.signal.data.repository.chat

import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.chat.chatdatabase.ChatDetailDao
import com.ongo.signal.data.repository.chat.chatdatabase.ChatHomeDao
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val chatHomeDao: ChatHomeDao,
    private val chatDetailDao: ChatDetailDao
) {
    suspend fun getAllChats(): List<ChatHomeDTO> = chatHomeDao.getAll()

    suspend fun insertChat(room: ChatHomeDTO) {
        chatHomeDao.insertAll(room)
    }

    suspend fun getAllMessages(ID: Int): List<ChatHomeChildDto> = chatDetailDao.getAll(ID)

    suspend fun insertMessage(message: ChatHomeChildDto) {
        chatDetailDao.insertMessage(message)
    }
}