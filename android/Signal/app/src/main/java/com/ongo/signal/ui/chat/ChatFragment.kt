package com.ongo.signal.ui.chat

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.ChatHomeChildDto
import com.ongo.signal.data.model.ChatHomeDTO
import com.ongo.signal.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChatFragment_싸피"

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {

    private lateinit var chatHomeAdapter : ChatHomeAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    override fun init() {
        binding.apply {

            val newItem = ChatHomeDTO(1, mutableListOf(ChatHomeChildDto(1,"11111", "1111", "20:30", 0)))
            chatViewModel.addLiveList(newItem)
            chatViewModel.addLiveList(ChatHomeDTO(2, mutableListOf(ChatHomeChildDto(2,"22222", "22222","20:30", 0)))
            )

            chatHomeAdapter = ChatHomeAdapter(chatListClickListner = {
                Log.d(TAG, "init: asdasdasda")
            })
            binding.chatHomeList.adapter = chatHomeAdapter

            lifecycleOwner?.let {
                chatViewModel.liveList.observe(it, Observer { chatList ->
                    chatHomeAdapter.submitList(chatList)
                })
            }
        }
    }
}