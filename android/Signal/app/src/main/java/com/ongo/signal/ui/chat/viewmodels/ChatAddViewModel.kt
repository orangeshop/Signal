package com.ongo.signal.ui.chat.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ongo.signal.data.model.chat.ChatAddListDTO
import com.ongo.signal.data.model.chat.ChatAddPeopleListDTO

class ChatAddViewModel : ViewModel() {
    private val _topList = MutableLiveData<List<ChatAddListDTO>>()
    val topList: MutableLiveData<List<ChatAddListDTO>> = _topList


    private val _profileList = MutableLiveData<List<ChatAddPeopleListDTO>>()
    val profileList: MutableLiveData<List<ChatAddPeopleListDTO>> = _profileList
}