package com.ongo.signal.ui.chat

import android.os.Build.VERSION_CODES.P
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.main.chat.ChatDetailDao
import com.ongo.signal.data.repository.main.chat.ChatHomeDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CompletableDeferred
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
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ChatHomeViewModel_싸피"

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatHomeDao: ChatHomeDao,
    private val chatDetailDao: ChatDetailDao
) : ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _messageList = MutableLiveData<List<ChatHomeChildDto>>()
    val messageList: LiveData<List<ChatHomeChildDto>> = _messageList

    var chatRoomNumber = 0


    fun claerMessageList() {
        _messageList.value = mutableListOf()
    }

    fun loadChats() {
        viewModelScope.launch {
            _liveList.value = chatHomeDao.getAll()
        }
    }

    fun saveChat(room: ChatHomeDTO) {

        viewModelScope.launch {
            chatHomeDao.insertAll(room)
            loadChats() // Refresh the list after saving a new chat
        }
    }

    fun LoadDetailList(ID: Int) {
        viewModelScope.launch {
            _messageList.value = chatDetailDao.getAll(ID)
        }
    }


    fun SaveDetailList(message: ChatHomeChildDto, id: Int) {
        viewModelScope.launch {
            chatDetailDao.insertMessage(message)
            LoadDetailList(id)
        }
    }

    fun timeSetting(): String {
        val now = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("a hh:mm", Locale.KOREAN).format(now)
        return simpleDateFormat
    }

    val client = StompClient(OkHttpWebSocketClient())

    var stompSession: StompSession? = null

    fun StompSend(item: ChatHomeChildDto) {


        val json: String = Gson().toJson(item)

        Log.d(TAG, "StompSend: ${json}")

        viewModelScope.launch {
            stompSession?.sendText(
                "/app/chat/send",
                "{\"message_id\":${item.message_id},\"chat_id\":${item.chat_id},\"is_from_sender\":${item.is_from_sender},\"content\":\"${item.content}\",\"is_read\":${item.read},\"send_at\":${null}}"
            )
        }
    }

    fun StompGet(chatRoomNumber : Int) {
        viewModelScope.launch {
            stompSession?.apply {
                val newChatMessage: Flow<StompFrame.Message> = subscribe(
                    StompSubscribeHeaders(
                        destination = "/topic/${chatRoomNumber}",
                        customHeaders = mapOf(

                        )
                    )
                )

                newChatMessage.collect {
                    Log.d(TAG, "STOMP Client newChatMessage: ${it.bodyAsText}")
                    val json = it.bodyAsText

                    val stompGetMessage: ChatHomeChildDto = Gson().fromJson(json, ChatHomeChildDto::class.java)



                    stompGetMessage.send_at = ""
//                    Log.d(TAG, "StompGet: ${test}")
                    SaveDetailList(stompGetMessage, stompGetMessage.chat_id)
                }
            }
        }
    }

    fun ConnectedWebSocket(chatRoomNumber : Int) {
        viewModelScope.launch {
            try {
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    ).build()

                val client = StompClient(
                    OkHttpWebSocketClient(okHttpClient)
                )

                stompSession = client.connect(
                    "ws://192.168.100.95:8080/chat",
                    customStompConnectHeaders = mapOf(

                    )
                )

                StompGet(chatRoomNumber)

            } catch (e: Exception) {
                Log.d(TAG, "ConnectedWebSocket: $e")
            }
        }
    }

    fun StompDisConnect() {
        viewModelScope.launch {
            stompSession?.disconnect()
        }
    }
}