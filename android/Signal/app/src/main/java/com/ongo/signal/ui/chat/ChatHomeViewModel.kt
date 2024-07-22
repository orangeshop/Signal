package com.ongo.signal.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.main.chat.ChatDetailDao
import com.ongo.signal.data.repository.main.chat.ChatHomeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
        
        for(i in 0 ..<(messageList.value?.size ?: 0)){
            Log.d(TAG, "LoadDetailList: ${messageList.value?.get(i)}")
        }
    }

    fun SaveDetailList(message : ChatHomeChildDto, id : Int){
        viewModelScope.launch {

            chatDetailDao.insertMessage(message)
            LoadDetailList(id)
        }
    }


//
//    fun addList(item : ChatHomeChildDto){
//        val current = _listDetailList.value ?: mutableListOf()
//
//        val update = current.toMutableList().apply {
//            add(item)
//        }
//
//        _listDetailList.value = update
////        Log.d(TAG, "addList: ${item} ${_listDetailList.value?.get(1)?.content}")
//        for(item in 0 ..<(listDetailList.value?.size ?: 0)){
//            Log.d(TAG, "${_listDetailList.value?.get(item)?.content}")
//        }
//    }


}