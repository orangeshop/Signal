package com.ongo.signal.ui.chat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.chat.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ChatHomeViewModel_싸피"

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,

) : ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _messageList = MutableLiveData<List<ChatHomeChildDto>>()
    val messageList: LiveData<List<ChatHomeChildDto>> = _messageList

    var chatRoomNumber = 0

    fun clearMessageList() {
        _messageList.value = emptyList()
    }

    fun loadChats() {
        viewModelScope.launch {
            _liveList.value = chatUseCases.loadChats().sortedByDescending { it.send_at }
        }
    }

    fun saveChat(room: ChatHomeDTO) {
        viewModelScope.launch {
            chatUseCases.saveChat(room)
            loadChats() // Refresh the list after saving a new chat
        }
    }

    fun loadDetailList(id: Int) {
        viewModelScope.launch {
            _messageList.value = chatUseCases.loadDetailList(id)
        }
    }

//    fun saveDetailList(message: ChatHomeChildDto, id: Int) {
//        viewModelScope.launch {
//            chatUseCases.saveDetailList(message, id)
//            loadDetailList(id)
//        }
//    }

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
            chatUseCases.stompGet(chatRoomNumber){ id ->
               loadDetailList(id)
            }
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


    fun timeSetting(time : String) : String {
        // DateTimeFormatter을 사용하여 입력된 날짜 문자열을 ZonedDateTime 객체로 파싱
        val inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val zonedDateTime = ZonedDateTime.parse(time, inputFormatter)

        // 한국 시간대로 변환
        val koreaZoneId = ZoneId.of("Asia/Seoul")
        val koreaTime = zonedDateTime.withZoneSameInstant(koreaZoneId)

        // 원하는 형식으로 포맷팅
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = koreaTime.format(outputFormatter)

        var result = ""


        if(formattedDate.split(" ")[0] == Date(System.currentTimeMillis()).toString()){
            result = formattedDate.split(" ")[1].substring(0,5)
            if(result.split(":")[0].toInt() > 12){
                result = "오후 " + formattedDate.split(" ")[1].substring(0,2).toInt().minus(12) +formattedDate.split(" ")[1].substring(2,5)
            }else{
                result = "오전 " + formattedDate.split(" ")[1].substring(0,5)
            }
        }
        else if (formattedDate.split(" ")[0] == Date(System.currentTimeMillis() - 86400000).toString()){
            result = "어제"
        }
        else{
            result = formattedDate.split(" ")[0]
        }


        return result
    }

}