package com.ongo.signal.data.repository.chat

import android.util.Log
import com.google.gson.Gson
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.chat.chatservice.ChatRepository
import com.ongo.signal.network.StompService
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.sendText
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
        Log.d(TAG, "loadChats: ${serverChatList}")
        if (serverChatList != null) {
            for(item in serverChatList){
                Log.d(TAG, "loadChats: ${item}")
                saveChat(item)
            }
        }

        return chatRoomRepositoryImpl.getAllChats()
    }

    override suspend fun saveChat(room: ChatHomeDTO) {
        chatRoomRepositoryImpl.insertChat(room)
    }

    override suspend fun loadDetailList(id: Int): List<ChatHomeChildDto> {


        return chatRoomRepositoryImpl.getAllMessages(id)
    }

    override suspend fun saveDetailList(message: ChatHomeChildDto, id: Int) {
        chatRoomRepositoryImpl.insertMessage(message)

    }

    override fun timeSetting(): String {
        val now = System.currentTimeMillis()
        return SimpleDateFormat("a hh:mm", Locale.KOREAN).format(now)
    }

    override suspend fun stompSend(item: ChatHomeChildDto) {
        val json: String = Gson().toJson(item)

        stompSession?.sendText(
            "/app/chat/send",
            "{\"message_id\":${item.message_id},\"chat_id\":${item.chat_id},\"is_from_sender\":${item.is_from_sender},\"content\":\"${item.content}\",\"is_read\":${item.read},\"send_at\":null}"
        )
    }

    override suspend fun stompGet(chatRoomNumber: Int, onSuccess: (Int) -> Unit){

        stompSession?.apply {
            val newChatMessage: Flow<StompFrame.Message> = subscribe(
                StompSubscribeHeaders(
                    destination = "/topic/$chatRoomNumber"
                )
            )

            newChatMessage.collect {
                val json = it.bodyAsText
                val stompGetMessage: ChatHomeChildDto = Gson().fromJson(json, ChatHomeChildDto::class.java)
                stompGetMessage.send_at = ""
                saveDetailList(stompGetMessage, stompGetMessage.chat_id)
                onSuccess(stompGetMessage.chat_id)
            }
        }
    }

    override suspend fun connectedWebSocket(chatRoomNumber: Int) {
        try {
            stompSession = stompService.connect("ws://192.168.100.95:8080/chat")
        } catch (e: Exception) {
            Log.d(TAG, "ConnectedWebSocket: $e")
        }
    }

    override suspend fun stompDisconnect() {
        stompSession?.disconnect()
    }
}
