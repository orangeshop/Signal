package com.ongo.signal.data.repository.chat

import com.google.gson.Gson
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.data.repository.chat.chatservice.ChatRepository
import com.ongo.signal.network.StompService
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.sendText
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ChatUseCasesImpl_싸피"

class ChatUseCasesImpl @Inject constructor(
    private val chatRoomRepositoryImpl: ChatRoomRepositoryImpl,
    private val stompService: StompService,
    private val chatRepository: ChatRepository
) : ChatUseCases {

    private var stompSession: StompSession? = null

    override suspend fun loadChats(): List<ChatHomeDTO> {
        val serverChatList = chatRepository.getChatList().body()
        if (serverChatList != null) {
            for(item in serverChatList){
                saveChat(item)
            }
        }

        return chatRoomRepositoryImpl.getAllChats()
    }

    override suspend fun saveChat(room: ChatHomeDTO) {
        chatRoomRepositoryImpl.insertChat(room)
    }

    override suspend fun loadDetailList(id: Long, loading: Long): List<ChatHomeChildDTO> {
        val serverMessageList = chatRepository.getAllMessages(id).body()
//
//        if(serverMessageList != null){
//            for(item in serverMessageList){
//                saveDetailList(item, id)
//            }
//        }

        if (serverMessageList != null) {
            chatRoomRepositoryImpl.insertMessageList(serverMessageList.toMutableList())
        }

        return chatRoomRepositoryImpl.getAllMessages(id, loading)
    }

    override suspend fun loadDetailListNetwork(id: Long): List<ChatHomeChildDTO> {
        return chatRepository.getAllMessages(id).body()?.toMutableList() ?: emptyList()
    }

    override suspend fun loadDetailListNoId(limit: Long): List<ChatHomeChildDTO> {
        return chatRoomRepositoryImpl.getAllMessagesNoId(limit)
    }


    override suspend fun saveDetailList(message: ChatHomeChildDTO, id: Long) {
        chatRoomRepositoryImpl.insertMessage(message)

    }

    override suspend fun readMessage(id: Long) {
        chatRepository.readMessage(id)
    }



    override fun timeSetting(): String {
        val now = System.currentTimeMillis()
        return SimpleDateFormat("a hh:mm", Locale.KOREAN).format(now)
    }

    override suspend fun stompSend(item: ChatHomeChildDTO, onSuccess: () -> Unit) {
//        val json: String = Gson().toJson(item)

        stompSession?.sendText(
            "/app/chat/send",
            "{\"message_id\":${item.messageId},\"chat_id\":${item.chatId},\"is_from_sender\":${item.isFromSender},\"content\":\"${item.content}\",\"is_read\":${item.isRead},\"send_at\":null}"
        )

        onSuccess()
    }

    override suspend fun stompGet(chatRoomNumber: Long, onSuccess: (Long) -> Unit){

        stompSession?.apply {
            val newChatMessage: Flow<StompFrame.Message> = subscribe(
                StompSubscribeHeaders(
                    destination = "/topic/$chatRoomNumber"
                )
            )

            newChatMessage.collect {
                val json = it.bodyAsText
                val stompGetMessage: ChatHomeChildDTO = Gson().fromJson(json, ChatHomeChildDTO::class.java)
                stompGetMessage.sendAt = ""
                saveDetailList(stompGetMessage, stompGetMessage.chatId)
                onSuccess(stompGetMessage.chatId)
            }
        }
    }

    override suspend fun connectedWebSocket(chatRoomNumber: Long) {
        try {
            stompSession = stompService.connect("ws://13.125.47.74:8080/chat")
        } catch (e: Exception) {
            Timber.tag(TAG).d("ConnectedWebSocket: %s", e)
        }
    }

    override suspend fun stompDisconnect() {
        stompSession?.disconnect()
    }

    override suspend fun saveLocalMessage(room: ChatHomeLocalCheckDTO) {
        return chatRoomRepositoryImpl.saveLocalMessage(room)
    }

    override suspend fun getLastMessageIndex(chatId: Long): ChatHomeLocalCheckDTO? {
        return chatRoomRepositoryImpl.getLastMessageIndex(chatId)
    }

    override fun updateLastReadMessageIndex(
        chatId: Long,
        lastReadMessageIndex: Long,
        sendAt: String
    ) {
        return chatRoomRepositoryImpl.updateLastReadMessageIndex(chatId, lastReadMessageIndex, sendAt)
    }

    override fun updateTodayFirstMessage(
        chatId: Long,
        todayFirstSendMessageId: Long,
        sendAt: String
    ) {
        return chatRoomRepositoryImpl.updateTodayFirstMessage(chatId, todayFirstSendMessageId, sendAt)
    }

    override fun updateMessageAmount(ChatId: Long, messageVolume: Long, sendAt: String) {
        return chatRoomRepositoryImpl.updateMessageAmount(ChatId, messageVolume, sendAt)
    }


}
