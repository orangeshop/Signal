package com.ongo.signal.ui.chat

import android.os.Build.VERSION_CODES.P
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.main.chat.ChatDetailDao
import com.ongo.signal.data.repository.main.chat.ChatHomeDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ChatHomeViewModel_싸피"

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatHomeDao: ChatHomeDao,
    private val chatDetailDao: ChatDetailDao
):ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _messageList = MutableLiveData<List<ChatHomeChildDto>>()
    val messageList: LiveData<List<ChatHomeChildDto>> = _messageList

    var chatRoomNumber = 0


    fun claerMessageList(){
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

    fun LoadDetailList(ID : Int){
        viewModelScope.launch {
            _messageList.value = chatDetailDao.getAll(ID)
        }
    }



    fun SaveDetailList(message : ChatHomeChildDto, id : Int){
        viewModelScope.launch {
            chatDetailDao.insertMessage(message)
            LoadDetailList(id)
        }
    }

    fun timeSetting(): String{
        val now = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("a hh:mm", Locale.KOREAN).format(now)
        return simpleDateFormat
    }

    val client = StompClient(OkHttpWebSocketClient())

    val moshi : Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    var stompSession : StompSession? = null

    fun ConnectedWebSocket(){
        viewModelScope.launch {
            try {
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor (
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

                stompSession?.apply {
                    val newChatMessage: Flow<StompFrame.Message> = subscribe(
                        StompSubscribeHeaders(
                            destination = "/topic/14",
                            customHeaders = mapOf(

                            )
                        )
                    )


                }



            }catch (e:Exception){
                Log.d(TAG, "ConnectedWebSocket: $e")
            }
        }
    }



}