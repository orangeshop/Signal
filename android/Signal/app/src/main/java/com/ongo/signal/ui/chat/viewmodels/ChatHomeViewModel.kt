package com.ongo.signal.ui.chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.data.repository.chat.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
) : ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _messageList = MutableLiveData<List<ChatHomeChildDTO>>()
    val messageList: LiveData<List<ChatHomeChildDTO>> = _messageList


    val todayTitleChecker = mutableListOf<Long>()


    var chatRoomNumber: Long = 0
    var chatRoomFromID: Long = 0
    var chatRoomToID: Long = 0

    var videoToID: Long = 0
    var videoToName: String = ""

    fun clearMessageList() {
        _messageList.value = emptyList()
    }

    fun loadChats() {
        viewModelScope.launch {
            _liveList.value = UserSession.userId?.let {
                chatUseCases.loadChats(it.toLong()).sortedByDescending { it.sendAt }
            }
//            _liveList.value =
//                UserSession.userId?.let { chatUseCases.loadChats(it, UserSession.userId!!).sortedByDescending { it.sendAt } }

        }
    }

//    fun saveChat(room: ChatHomeDTO) {
//        viewModelScope.launch {
//            chatUseCases.saveChat(room)
//            loadChats() // Refresh the list after saving a new chat
//        }
//    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            chatUseCases.deleteChat(id)

            val currentList = _liveList.value.orEmpty().toMutableList()

            liveList.value?.map {
                if (it.chatId == id) {
                    currentList.remove(it)
                    // 서버에서 채팅 삭제 로직 추가ㅁㄴ
                    _liveList.value = currentList
                }
            }
        }
    }

    /**
     * 기존의 코드
     * messageList <- room에서 detailList의 모든 리스트를 들고와서 갱신함
     *
     * 개선 방향
     * 기존의 코드에서 messageList 이후의 메시지만 라이브 데이터에 올리는 방식으로 로직 변경
     *
     * 내가 보낸 메시지는 바로 viewmodel에 넣는다.
     *
     *
     *
     **/

    suspend fun loadDetailList(id: Long, loading: Long = 100) {
        CoroutineScope(Dispatchers.IO).launch {
            _messageList.postValue(
                chatUseCases.loadDetailList(id, loading).sortedBy { it.messageId })
        }
    }

    fun readMessage(id: Long) {
        viewModelScope.launch {
            chatUseCases.readMessage(id)
//            loadDetailList(id)
        }
    }

//    fun appendDetailList(message: ChatHomeChildDto) {
//        val currentList = _messageList.value.orEmpty().toMutableList()
//        currentList.add(message)
//        _messageList.value = currentList
//    }
//
//
//    fun saveDetailList(message: ChatHomeChildDto, id: Long) {
//        viewModelScope.launch {
//            chatUseCases.saveDetailList(message, id)
//            loadDetailList(id)
//        }
//    }

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


}