package com.ongo.signal.ui.chat.fragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.internal.ViewUtils.dpToPx
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.repository.main.chatnetworking.SaveDetailCallback
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.ChatHomeViewModel
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "ChatDetailFragment_싸피"

class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding>(R.layout.fragment_chat_detail) {

    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    @SuppressLint("RestrictedApi")
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
                if(binding.etSearch.text.toString() != "") {


                    chatViewModel.SaveDetailList(
                        ChatHomeChildDto(
                            0,
                            chatViewModel.chatRoomNumber,
                            1,
                            1,
                            false,
                            binding.etSearch.text.toString(),
                            false,
                            chatViewModel.timeSetting()
                        ), chatViewModel.chatRoomNumber)

                    binding.etSearch.text.clear()

                    lifecycleScope.launch {
                        delay(200)
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