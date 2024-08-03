package com.ongo.signal.ui.chat.fragment

import android.os.Build.VERSION_CODES.P
import android.text.TextUtils.substring
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.text.Typography.tm

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
        (requireActivity() as? MainActivity)?.hideBottomNavigation()

        binding.apply {
            
            chatViewModel.connectedWebSocket(chatViewModel.chatRoomNumber)

            chatDetailAdapter = ChatDetailAdapter(
                timeSetting = {time,target ->
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val date: Date = isoFormat.parse(time) ?: Date(System.currentTimeMillis())

                    // 원하는 출력 형식의 포맷터
                    val outputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("GMT")
                    }

                    var result = ""

                    if (date != null) {
                        val time = outputFormat.format(date)
                        result = chatViewModel.timeSetting(time, target)
                    }
                    result
                },
                todaySetting = {id, item, time ->
                    var result = false

                    /*
                    리스트가 마운트 될 때 배열에 날이 바뀌는 인덱스를 넣어야 함
                    그리고 해당 인덱스가 있는지 뽑아내는 방식이면 충분할 듯 함
                    로컬 배열에 값을 넣자
                    * */

                    var tmp = chatViewModel.messageList.value?.get(chatViewModel.messageList.value!!.lastIndex)?.sendAt


//                    
                    for(item in chatViewModel.messageList.value!!){
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

                            if(preDayOutput.substring(1,8) != todayOutput.substring(1,8)){
                                todayTitleChecker.add(item.messageId)
                            }
                        }
                        tmp = item.sendAt
                    }
                    if(todayTitleChecker.contains(id)){
                        result = true
                    }

                    result
                }, chatViewModel.chatRoomFromID
            )

            binding.chatDetailRv.adapter = chatDetailAdapter

            lifecycleOwner?.let {
                chatViewModel.messageList.observe(it, Observer { chatList ->
                    chatDetailAdapter.submitList(chatList){
                        binding.chatDetailRv.scrollToPosition(
                            chatList.lastIndex
                        )
                    }
                })
            }

            binding.chatDetailBtn.setOnClickListener {
                if(binding.etSearch.text.toString() != "") {
                    val message = ChatHomeChildDTO(
                        0,
                        chatViewModel.chatRoomNumber,
                        chatViewModel.chatRoomFromID == UserSession.userId,
                        binding.etSearch.text.toString(),
                        false,
                        chatViewModel.timeSetting(Date(System.currentTimeMillis()).toString(), 1)
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