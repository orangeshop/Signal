package com.ongo.signal.ui.chat.fragment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.setupActionBarWithNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ChatDetailFragment_싸피"

/**
 * 해당 클래스는 채팅 내역을 보여주는 화면입니다.
 *
 */
class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding>(R.layout.fragment_chat_detail) {

    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    override fun init() {

        Log.d(TAG, "@@@@@@@@@@@@@@@@ vChatDetailFragment : ")
        (requireActivity() as? MainActivity)?.showBottomNavigation()
        (requireActivity() as? MainActivity)?.hideBottomNavigation()

        binding.apply {

            chatViewModel.connectedWebSocket(chatViewModel.chatRoomNumber)

            chatDetailAdapter = ChatDetailAdapter()
            binding.chatDetailRv.adapter = chatDetailAdapter

            lifecycleOwner?.let {
                chatViewModel.messageList.observe(it, Observer { chatList ->
                    chatDetailAdapter.submitList(chatList)
                })
            }

            binding.chatDetailBtn.setOnClickListener {
                if(binding.etSearch.text.toString() != "") {
//                    chatViewModel.saveDetailList(
//                        ChatHomeChildDto(
//                            0,
//                            chatViewModel.chatRoomNumber,
//                            1,
//                            1,
//                            false,
//                            binding.etSearch.text.toString(),
//                            false,
//                            chatViewModel.timeSetting()
//                        ), chatViewModel.chatRoomNumber)

                    chatViewModel.stompSend(ChatHomeChildDto(
                        0,
                        chatViewModel.chatRoomNumber,
                        1,
                        1,
                        false,
                        binding.etSearch.text.toString(),
                        false,
                        chatViewModel.timeSetting()
                    ))

                    binding.etSearch.text.clear()

                    // 헤당 부분 콜백을 받아서 처리되도록 수정해야함
                    lifecycleScope.launch {
                        delay(1000)
                        chatViewModel.messageList.value?.let { it1 ->
                            binding.chatDetailRv.smoothScrollToPosition(
                                it1.lastIndex )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}