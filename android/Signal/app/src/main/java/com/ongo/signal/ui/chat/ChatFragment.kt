package com.ongo.signal.ui.chat

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChatFragment_싸피"

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {

    private lateinit var chatHomeAdapter: ChatHomeAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    override fun init() {
        binding.apply {
//            삭제 예쩡
//            val newItem =
//                ChatHomeDTO(1,  MutableLiveData(listOf(ChatHomeChildDto(2, "22222", "22222", "20:30", 0))))
//            chatViewModel.addLiveList(newItem)
//            chatViewModel.addLiveList(
//                ChatHomeDTO(2, mutableListOf(ChatHomeChildDto(2, "22222", "22222", "20:30", 0)))
//            )

            chatViewModel.loadChats()
            var cnt = 0
            chatHomeTestBtn.setOnClickListener {
                chatViewModel.saveChat(ChatHomeDTO(cnt, mutableListOf(ChatHomeChildDto(1,"123", "123", "123", 123))))
                chatViewModel.loadChats()
                cnt += 1
            }

            chatHomeAdapter = ChatHomeAdapter(
                chatItemClick = {

            },
                chatItemLongClick = {

                    true
                }
            )
            binding.chatHomeList.adapter = chatHomeAdapter

            lifecycleOwner?.let {
                chatViewModel.liveList.observe(it, Observer { chatList ->
                    chatHomeAdapter.submitList(chatList)
                })
            }



            // Example: Add a new chat item

        }
    }
}