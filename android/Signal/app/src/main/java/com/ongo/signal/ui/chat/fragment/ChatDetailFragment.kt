package com.ongo.signal.ui.chat.fragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    private val todayTitleChecker = mutableSetOf<Long>()
    override fun init() {

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.hideBottomNavigation()

        loading()

        binding.apply {

            chatViewModel.connectedWebSocket(chatViewModel.chatRoomNumber)

            lifecycleScope.launch {
                chatDetailAdapter = ChatDetailAdapter(
                    timeSetting = { time, target ->
                        val isoFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                        val date: Date = isoFormat.parse(time) ?: Date(System.currentTimeMillis())

                        // 원하는 출력 형식의 포맷터
                        val outputFormat =
                            SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.ENGLISH).apply {
                                timeZone = TimeZone.getTimeZone("GMT")
                            }

                        var result = ""

                        if (date != null) {
                            val time = outputFormat.format(date)
                            result = chatViewModel.timeSetting(time, target)
                        }
                        result
                    },
                    todaySetting = { id, item, time ->
                        var result = false

                        /*
                        리스트가 마운트 될 때 배열에 날이 바뀌는 인덱스를 넣어야 함
                        그리고 해당 인덱스가 있는지 뽑아내는 방식이면 충분할 듯 함
                        로컬 배열에 값을 넣자
                        * */

                        var tmp =
                            chatViewModel.messageList.value?.get(chatViewModel.messageList.value!!.lastIndex)?.sendAt

                        for (item in chatViewModel.messageList.value!!) {
                            if (tmp != null) {

                                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                                val outputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                                TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
                                outputFormat.timeZone = TimeZone.getDefault()

                                val preDay = inputFormat.parse(tmp)
                                val preDayOutput = outputFormat.format(preDay)

                                val Today = inputFormat.parse(item.sendAt)
                                val todayOutput = outputFormat.format(Today)

                                if (preDayOutput.substring(1, 8) != todayOutput.substring(1, 8)) {
                                    todayTitleChecker.add(item.messageId)
                                }
                            }
                            tmp = item.sendAt
                        }
                        if (todayTitleChecker.contains(id)) {
                            result = true
                        }

                        result
                    },
                    chatViewModel.chatRoomFromID
                )
            }


            binding.chatDetailRv.adapter = chatDetailAdapter
            var check = true
            lifecycleOwner?.let {
                chatViewModel.messageList.observe(it, Observer { chatList ->
                    chatDetailAdapter.submitList(chatList) {

                        chatViewModel.readMessage(chatViewModel.chatRoomNumber)

                        val isFrom = UserSession.userId == chatViewModel.chatRoomFromID

                        if (chatList.isNotEmpty() && check == false) {
                            if (isFrom != chatList.get(chatList.lastIndex).isFromSender) {
                                lifecycleScope.launch {
                                    binding.newMessage.visibility = View.VISIBLE
                                    binding.newMessageTv.text =
                                        chatList.get(chatList.lastIndex).content
                                }
                            }
                        }

                        if(chatList.size > 1) {
                            progressBar.progress = chatList.size
                        }
                        if(progressBar.progress == chatList.size && chatList.size != 0){
                            progressBar.visibility = View.GONE
                        }

                        if (chatList.isNotEmpty() && check) {
                            lifecycleScope.launch {
                                binding.chatDetailRv.scrollToPosition(
                                    if (chatViewModel.getLastReadMessage(chatViewModel.chatRoomNumber) + 300 <= chatList.lastIndex) {
                                        chatList.lastIndex

                                    } else {
                                        chatList.lastIndex
                                    }
                                )
                            }
                            check = false
                        }

                        if (chatList.isNotEmpty() && check == false && chatList.get(chatList.lastIndex).isFromSender == isFrom) {
                            lifecycleScope.launch {
                                chatViewModel.messageList.value?.let { it1 ->
                                    binding.chatDetailRv.smoothScrollToPosition(
                                        it1.lastIndex
                                    )
                                }
                            }
                        }
                    }
                })
            }

            etSearch.setOnTouchListener { v, event ->

                etSearch.isFocusable = true
                etSearch.requestFocus()
                etSearch.setSelection(etSearch.text.length)

                lifecycleScope.launch {
                    val manager = binding.chatDetailRv.layoutManager as LinearLayoutManager

                    manager.apply {
                        val num = findLastVisibleItemPosition()
                        delay(100)
                        binding.chatDetailRv.scrollToPosition(
                            if (num <= 15) 0 else num
                        )
                    }
                }
                false
            }

            binding.newMessage.setOnClickListener {
                binding.newMessage.visibility = View.GONE
                binding.newMessageTv.text = ""

                lifecycleScope.launch {
                    chatViewModel.messageList.value?.let { it1 ->
                        binding.chatDetailRv.smoothScrollToPosition(
                            it1.lastIndex
                        )
                    }
                }
            }

            binding.chatDetailBtn.setOnClickListener {
                if (binding.etSearch.text.toString() != "") {
                    val message = ChatHomeChildDTO(
                        0,
                        chatViewModel.chatRoomNumber,
                        chatViewModel.chatRoomFromID == UserSession.userId,
                        binding.etSearch.text.toString(),
                        false,
                        chatViewModel.timeSetting(Date(System.currentTimeMillis()).toString(), 1)
                    )

                    binding.etSearch.text.clear()

                    chatViewModel.stompSend(message) {}
                }
            }
        }
    }

    fun loading() {
        lifecycleScope.launch {
            binding.chatDetailRv.visibility = View.GONE



//            binding.progressBar.visibility = View.GONE
            binding.chatDetailRv.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()

        chatViewModel.saveLocalDataMessage(
            ChatHomeLocalCheckDTO(
                chatViewModel.chatRoomNumber,
                0,
                0,
                chatViewModel.messageList.value?.size?.toLong() ?: 0,
                0,
                ""
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}