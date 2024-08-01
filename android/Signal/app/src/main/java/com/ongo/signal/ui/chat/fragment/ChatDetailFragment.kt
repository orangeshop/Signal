package com.ongo.signal.ui.chat.fragment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.setupActionBarWithNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val TAG = "ChatDetailFragment_싸피"

/**
 * 해당 클래스는 채팅 내역을 보여주는 화면입니다.
 *
 */
class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding>(R.layout.fragment_chat_detail) {

    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()

    override fun init() {
        (requireActivity() as? MainActivity)?.hideBottomNavigation()

        binding.apply {

            chatViewModel.connectedWebSocket(chatViewModel.chatRoomNumber)

            chatDetailAdapter = ChatDetailAdapter(
                timeSetting = {time,target ->
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val date: Date? = isoFormat.parse(time)

                    // 원하는 출력 형식의 포맷터
                    val outputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("GMT")
                    }

                    var result = ""

                    if (date !=     null) {
                        val time = outputFormat.format(date)
                        result = chatViewModel.timeSetting(time, target)

                    }

                    result

                }
            )
            binding.chatDetailRv.adapter = chatDetailAdapter

            lifecycleOwner?.let {
                chatViewModel.messageList.observe(it, Observer { chatList ->
                    chatDetailAdapter.submitList(chatList)
                })
            }

            binding.chatDetailBtn.setOnClickListener {
                if(binding.etSearch.text.toString() != "") {

                    Log.d(TAG, "init: @@@@@@@@@@@@@@@@@@ ${chatViewModel.chatRoomFromID} ${UserSession.userId}")
                    val message = ChatHomeChildDto(
                        0,
                        chatViewModel.chatRoomNumber,
                        chatViewModel.chatRoomFromID == UserSession.userId,
                        binding.etSearch.text.toString(),
                        false,
                        chatViewModel.timeSetting()
                    )

                    binding.etSearch.text.clear()

                    chatViewModel.stompSend(message){
                        // 헤당 부분 콜백을 받아서 처리되도록 수정해야함
                        lifecycleScope.launch {
                            chatViewModel.messageList.value?.let { it1 ->
                                binding.chatDetailRv.smoothScrollToPosition(
                                    it1.lastIndex )
                            }
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