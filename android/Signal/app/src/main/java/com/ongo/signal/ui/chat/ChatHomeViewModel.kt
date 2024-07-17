package com.ongo.signal.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ongo.signal.data.model.chat.ChatHomeDTO

class ChatHomeViewModel:ViewModel() {

    private val _liveList =  MutableLiveData<List<ChatHomeDTO>>()
    val liveList :  MutableLiveData<List<ChatHomeDTO>> = _liveList

    fun addLiveList(item : ChatHomeDTO){
        val list = _liveList.value?.toMutableList() ?: mutableListOf()
        list.add(item)
        _liveList.value = list
    }

    fun replaceLiveList(idx: Int, item : ChatHomeDTO){
        val list = _liveList.value?.toMutableList() ?: mutableListOf()
        list.removeAt(idx)
        list.add(0, item)
        _liveList.value = list
    }
}