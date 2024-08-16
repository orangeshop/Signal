package com.ongo.signal.data.repository.chat

import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.data.repository.chat.chatdatabase.ChatDetailDao
import com.ongo.signal.data.repository.chat.chatdatabase.ChatHomeDao
import com.ongo.signal.data.repository.chat.chatdatabase.ChatHomeLocalCheckDao
import javax.inject.Inject

class ChatRoomRepositoryImpl @Inject constructor(
    private val chatHomeDao: ChatHomeDao,
    private val chatDetailDao: ChatDetailDao,
    private val chatHomeLocalCheckDao: ChatHomeLocalCheckDao
) {
    suspend fun getAllChats(id: Long): List<ChatHomeDTO> {
        return chatHomeDao.getAll(id)
    }

    suspend fun insertChat(room: ChatHomeDTO) {
        chatHomeDao.insertAll(room)
    }

    suspend fun getAllMessages(ID: Long, loading: Long): List<ChatHomeChildDTO> =
        chatDetailDao.getAll(ID, loading)

    suspend fun getAllMessagesNoId(loading: Long): List<ChatHomeChildDTO> =
        chatDetailDao.getAllMessage(loading)

    suspend fun insertMessage(message: ChatHomeChildDTO) {
        chatDetailDao.insertMessage(message)
    }

    suspend fun insertMessageList(message: List<ChatHomeChildDTO>) {
        chatDetailDao.insertListMessage(message)
    }

    suspend fun saveLocalMessage(room: ChatHomeLocalCheckDTO) {
        chatHomeLocalCheckDao.saveLocalMessage(room)
    }

    suspend fun loadReadMessage(id : Long) : Int{
        return chatDetailDao.loadReadMessage(id)
    }

    suspend fun getLastMessageIndex(chatId: Long): ChatHomeLocalCheckDTO? {
        return chatHomeLocalCheckDao.getLastMessageIndex(chatId)
    }

    fun updateLastReadMessageIndex(chatId: Long, lastReadMessageIndex: Long, sendAt: String) {
        return chatHomeLocalCheckDao.updateLastReadMessageIndex(
            chatId,
            lastReadMessageIndex,
            sendAt
        )
    }

    fun updateTodayFirstMessage(chatId: Long, todayFirstSendMessageId: Long, sendAt: String) {
        return chatHomeLocalCheckDao.updateTodayFirstMessage(
            chatId,
            todayFirstSendMessageId,
            sendAt
        )
    }

    fun updateMessageAmount(chatId: Long, messageVolume: Long, sendAt: String) {
        return chatHomeLocalCheckDao.updateMessageAmount(chatId, messageVolume, sendAt)
    }

    suspend fun deleteChat(id : Long){
        chatHomeDao.delete(id)
    }
}