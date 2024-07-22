package com.ongo.signal.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.main.chat.ChatHomeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "ChatHomeViewModel_싸피"

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatHomeDao: ChatHomeDao
):ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

    private val _listDetailList = MutableLiveData<List<ChatHomeChildDto>>()
    val listDetailList: LiveData<List<ChatHomeChildDto>> = _listDetailList

    fun loadChats() {
        viewModelScope.launch {
            _liveList.value = chatHomeDao.getAll()
        }
    }

    fun saveChat(chat: ChatHomeDTO) {
        viewModelScope.launch {
            chatHomeDao.insertAll(chat)
            loadChats() // Refresh the list after saving a new chat
        }
    }

    fun getList(ID : Int){
        viewModelScope.launch {
            _listDetailList.value = chatHomeDao.getList(ID).list
            Log.d(TAG, "getList: ${listDetailList.value?.get(0)?.content}")
        }
    }

    fun addList(item : ChatHomeChildDto){
        val current = _listDetailList.value ?: mutableListOf()

        val update = current.toMutableList().apply {
            add(item)
        }

        _listDetailList.value = update
//        Log.d(TAG, "addList: ${item} ${_listDetailList.value?.get(1)?.content}")
        for(item in 0 ..<(listDetailList.value?.size ?: 0)){
            Log.d(TAG, "${_listDetailList.value?.get(item)?.content}")
        }
    }


}