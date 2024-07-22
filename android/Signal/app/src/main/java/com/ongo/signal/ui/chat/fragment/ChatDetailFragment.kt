package com.ongo.signal.ui.chat.fragment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.ChatHomeViewModel
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import timber.log.Timber

private const val TAG = "ChatDetailFragment_싸피"

class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding>(R.layout.fragment_chat_detail) {

    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    override fun init() {
        (requireActivity() as? MainActivity)?.hideBottomNavigation()



        binding.apply {
            chatDetailAdapter = ChatDetailAdapter()
            binding.chatDetailRv.adapter = chatDetailAdapter

            lifecycleOwner?.let {
                chatViewModel.messageList.observe(it, Observer { chatList ->
                    Timber.d("확인 ${chatList}")
                    chatDetailAdapter.submitList(chatList)
                })
            }

            binding.chatDetailBtn.setOnClickListener {
                Log.d(TAG, "init: ")
                chatViewModel.SaveDetailList(ChatHomeChildDto(0,chatViewModel.chatRoomNumber,1,1, false, "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222", false, ""), chatViewModel.chatRoomNumber)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}