package com.ongo.signal.ui.chat.fragment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.FragmentChatBinding
import com.ongo.signal.ui.chat.adapter.ChatHomeAdapter
import com.ongo.signal.ui.chat.ChatHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChatFragment_싸피"

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {

    private lateinit var chatHomeAdapter: ChatHomeAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    override fun init() {
        binding.apply {

            chatViewModel.loadChats()



            chatHomeFab.setOnClickListener {
                findNavController().navigate(R.id.action_chatFragment_to_chatAddFragment)

                chatViewModel.saveChat(
                    ChatHomeDTO(
                        0, 1, 2, "last", "status"
                    )
                )
            }

            chatHomeAdapter = ChatHomeAdapter(
                chatItemClick = {
                    chatViewModel.chatRoomNumber = it.chat_id
                    chatViewModel.LoadDetailList(it.chat_id)
                    findNavController().navigate(R.id.action_chatFragment_to_chatDetailFragment)
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

    override fun onResume() {
        super.onResume()
        chatViewModel.claerMessageList()
    }
}