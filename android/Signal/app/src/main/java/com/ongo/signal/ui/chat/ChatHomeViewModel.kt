package com.ongo.signal.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.chat.chatdatabase.ChatDetailDao
import com.ongo.signal.data.repository.chat.chatdatabase.ChatHomeDao
import com.ongo.signal.data.repository.chat.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ChatHomeViewModel_싸피"

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _messageList = MutableLiveData<List<ChatHomeChildDto>>()
    val messageList: LiveData<List<ChatHomeChildDto>> = _messageList

    var chatRoomNumber = 0

    fun clearMessageList() {
        chatUseCases.clearMessageList()
        _messageList.value = emptyList()
    }

    fun loadChats() {
        viewModelScope.launch {
            _liveList.value = chatUseCases.loadChats()
        }
    }

    fun saveChat(room: ChatHomeDTO) {
        viewModelScope.launch {
            chatUseCases.saveChat(room)
            loadChats() // Refresh the list after saving a new chat
        }
    }

    fun loadDetailList(ID: Int) {
        viewModelScope.launch {
            _messageList.value = chatUseCases.loadDetailList(ID)
        }
    }

    fun saveDetailList(message: ChatHomeChildDto, id: Int) {
        viewModelScope.launch {
            chatUseCases.saveDetailList(message, id)
            loadDetailList(id)
        }
    }

    fun timeSetting(): String {
        return chatUseCases.timeSetting()
    }

    fun stompSend(item: ChatHomeChildDto) {
        viewModelScope.launch {
            chatUseCases.stompSend(item)
        }
    }

    fun stompGet(chatRoomNumber: Int) {
        viewModelScope.launch {
            chatUseCases.stompGet(chatRoomNumber)
            loadDetailList(chatRoomNumber)
        }
    }

    fun connectedWebSocket(chatRoomNumber: Int) {
        viewModelScope.launch {
            chatUseCases.connectedWebSocket(chatRoomNumber)
            stompGet(chatRoomNumber)
        }
    }

    fun stompDisconnect() {
        viewModelScope.launch {
            chatUseCases.stompDisconnect()
        }
    }
}