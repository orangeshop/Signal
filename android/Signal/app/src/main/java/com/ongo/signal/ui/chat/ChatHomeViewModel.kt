package com.ongo.signal.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.repository.main.chat.ChatHomeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val chatHomeDao: ChatHomeDao
):ViewModel() {

    private val _liveList = MutableLiveData<List<ChatHomeDTO>>()
    val liveList: LiveData<List<ChatHomeDTO>> = _liveList

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
}