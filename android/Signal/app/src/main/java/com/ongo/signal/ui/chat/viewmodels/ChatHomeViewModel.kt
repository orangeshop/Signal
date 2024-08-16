package com.ongo.signal.ui.chat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.data.model.match.MatchAcceptResponse
import com.ongo.signal.data.model.match.MatchProposeResponse
import com.ongo.signal.data.repository.chat.ChatUseCases
import com.ongo.signal.data.repository.match.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

private const val TAG = "ChatHomeViewModel_싸피"

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val matchRepository: MatchRepository,
) : ViewModel() {

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}\n\n${throwable.stackTrace}")
        }

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _messageList = MutableLiveData<List<ChatHomeChildDTO>>()
    val messageList: LiveData<List<ChatHomeChildDTO>> = _messageList

    private val _messageReadList = MutableLiveData<List<Pair<Long, Long>>>()
    val messageReadList: LiveData<List<Pair<Long, Long>>> = _messageReadList

    val todayTitleChecker = mutableListOf<Long>()

    var chatRoomNumber: Long = 0
    var chatRoomFromID: Long = 0
    var chatRoomToID: Long = 0
    var chatRoomTitle: String = ""
    var chatRoomUrl: String = ""

    var videoToID: Long = 0
    var videoToName: String = ""

    fun clearMessageList() {
        _messageList.value = emptyList()
    }

//    fun loadChatProfile(){
//        viewModelScope.launch {
//            val currentList = _liveList.value.orEmpty().toMutableList()
//
//            for(i in currentList){
//                val update = chatUseCases.getUserProfile(i.toId)
//            }
//            _liveList.value = currentList
//
//        }
//    }

    fun loadChats() {
        viewModelScope.launch {
            _liveList.value = UserSession.userId?.let {
                chatUseCases.loadChats(it.toLong()).sortedByDescending { it.sendAt }
            }
        }
    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            chatUseCases.deleteChat(id)

            val currentList = _liveList.value.orEmpty().toMutableList()

            liveList.value?.map {
                if (it.chatId == id) {
                    currentList.remove(it)
                    // 서버에서 채팅 삭제 로직 추가ㅁㄴ
                    chatUseCases.deleteChatRoom(id)
                    _liveList.value = currentList
                }
            }
        }
    }

    suspend fun loadDetailList(id: Long, loading: Long = 100) {
        CoroutineScope(Dispatchers.IO).launch {
            _messageList.postValue(
                chatUseCases.loadDetailList(id, loading).sortedBy { it.messageId })
        }
    }

    fun readMessage(id: Long, userId: Long) {
        viewModelScope.launch {
            chatUseCases.readMessage(id, userId)
        }
    }

    fun timeSetting(): String {
        return chatUseCases.timeSetting()
    }

    fun stompSend(item: ChatHomeChildDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            chatUseCases.stompSend(item) {
                onSuccess()
            }
        }
    }

    fun stompGet(chatRoomNumber: Long) {
        viewModelScope.launch {
            chatUseCases.stompGet(chatRoomNumber) { id ->
                viewModelScope.launch {
                    loadDetailList(id)
                }
            }
        }
    }

    fun connectedWebSocket(chatRoomNumber: Long) {
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

    fun saveLocalDataMessage(room: ChatHomeLocalCheckDTO) {
        viewModelScope.launch {
            chatUseCases.saveLocalMessage(room)
        }
    }

    suspend fun getLastReadMessage(id: Long): Long {
        var test = CompletableDeferred<Long>()

        viewModelScope.launch {

            test.complete(chatUseCases.getLastMessageIndex(id)?.lastReadMessageIndex ?: 0)
        }
        return test.await()
    }

    suspend fun loadReadMessage(chatId: Long) {
        loadDetailList(chatId, 300)
        viewModelScope.launch {

            val currentList = _messageReadList.value.orEmpty().toMutableList()
            val update = chatUseCases.loadReadMessage(chatId)
            currentList.clear()
            currentList.add(Pair(chatId, update.toLong()))
            _messageReadList.value = currentList

            Log.d(TAG, "loadReadMessage: ${messageReadList.value}")
        }
    }


    fun timeSetting(time: String, target: Int): String {


//        Log.d(TAG, "timeSetting: ${time}")

        // DateTimeFormatter을 사용하여 입력된 날짜 문자열을 ZonedDateTime 객체로 파싱
        val inputFormatter =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val zonedDateTime = ZonedDateTime.parse(time, inputFormatter)

        // 한국 시간대로 변환
        val koreaZoneId = ZoneId.of("Asia/Seoul")
        val koreaTime = zonedDateTime.withZoneSameInstant(koreaZoneId)

        // 원하는 형식으로 포맷팅
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = koreaTime.format(outputFormatter)

        var result = ""


        if (target == 0) {
            if (formattedDate.split(" ")[0] == Date(System.currentTimeMillis()).toString()) {
                result = formattedDate.split(" ")[1].substring(0, 5)
                if (result.split(":")[0].toInt() >= 12) {
                    if (result.split(":")[0].toInt() == 12) {
                        result = "오후 " + formattedDate.split(" ")[1].substring(
                            0,
                            2
                        ) + formattedDate.split(" ")[1].substring(2, 5)
                    } else {
                        result = "오후 " + formattedDate.split(" ")[1].substring(0, 2).toInt()
                            .minus(12) + formattedDate.split(" ")[1].substring(2, 5)
                    }
                } else {
                    result = "오전 " + formattedDate.split(" ")[1].substring(0, 5)
                }
            } else if (formattedDate.split(" ")[0] == Date(System.currentTimeMillis() - 86400000).toString()) {
                result = "어제"
            } else {
                result = formattedDate.split(" ")[0]
            }
        } else if (target == 1) {
            result = formattedDate.split(" ")[1].substring(0, 5)
            if (result.split(":")[0].toInt() >= 12) {
                if (result.split(":")[0].toInt() == 12) {
                    result = "오후 " + formattedDate.split(" ")[1].substring(
                        0,
                        2
                    ) + formattedDate.split(" ")[1].substring(2, 5)
                } else {
                    result = "오후 " + formattedDate.split(" ")[1].substring(0, 2).toInt()
                        .minus(12) + formattedDate.split(" ")[1].substring(2, 5)
                }
            } else {
                result = "오전 " + formattedDate.split(" ")[1].substring(0, 5)
            }
        } else if (target == 2) {
            result = formattedDate.split(" ")[0]
        }
        return result
    }

    fun todayTitleSetting() {

        todayTitleChecker.clear()

        var tmp = messageList.value?.get(messageList.value!!.lastIndex)?.sendAt

        for (item in messageList.value!!) {
            if (tmp != null) {

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                val outputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
                outputFormat.timeZone = TimeZone.getDefault()

                val preDay = inputFormat.parse(tmp)
                val preDayOutput = outputFormat.format(preDay)

                val Today = inputFormat.parse(item.sendAt)
                val todayOutput = outputFormat.format(Today)

                if (preDayOutput.substring(1, 8) != todayOutput.substring(1, 8)) {
                    todayTitleChecker.add(item.messageId)
                }
            }
            tmp = item.sendAt
        }
    }

    fun postProposeVideoCall(
        fromId: Long,
        toId: Long,
        onSuccess: (MatchProposeResponse) -> Unit,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postProposeVideoCall(fromId, toId).onSuccess { response ->
                response?.let {
                    onSuccess(it)
                }
            }.onFailure { throw it }
        }
    }

    fun postProposeVideoCallAccept(
        fromId: Long,
        toId: Long,
        flag: Int,
        onSuccess: (MatchAcceptResponse) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            matchRepository.postProposeVideoCallAccept(fromId, toId, flag).onSuccess { response ->
                response?.let {
                    onSuccess(response)
                }
            }
        }
    }


}