package com.ongo.signal.ui.chat.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.adapter.ChatDetailAdapter
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import com.ongo.signal.ui.video.CallActivity
import com.ongo.signal.ui.video.repository.VideoRepository
import com.ongo.signal.ui.video.service.VideoService
import com.ongo.signal.ui.video.service.VideoServiceRepository
import com.ongo.signal.ui.video.util.DataModel
import com.ongo.signal.ui.video.util.DataModelType
import com.ongo.signal.ui.video.util.getCameraAndMicPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

private const val TAG = "ChatDetailFragment_싸피"

/**
 * 해당 클래스는 채팅 내역을 보여주는 화면입니다.
 *
 */
@AndroidEntryPoint
class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding>(R.layout.fragment_chat_detail),
    VideoService.Listener {

    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val chatViewModel: ChatHomeViewModel by activityViewModels()
    private val todayTitleChecker = mutableSetOf<Long>()

    //video
    @Inject
    lateinit var videoRepository: VideoRepository

    @Inject
    lateinit var videoServiceRepository: VideoServiceRepository

    override fun init() {
        startVideoService()
    }

    private fun startVideoService() {
        VideoService.listener = this
        videoServiceRepository.startService(UserSession.userId.toString())
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
                            SimpleDateFormat(
                                "EEE MMM dd HH:mm:ss 'GMT' yyyy",
                                Locale.ENGLISH
                            ).apply {
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


            chatDetailRv.adapter = chatDetailAdapter
            var check = true
            lifecycleOwner?.let {
                chatViewModel.messageList.observe(it, Observer { chatList ->
                    chatDetailAdapter.submitList(chatList) {

                        chatViewModel.readMessage(chatViewModel.chatRoomNumber)

                        val isFrom = UserSession.userId == chatViewModel.chatRoomFromID

                        if (chatList.isNotEmpty() && check == false) {
                            if (isFrom != chatList.get(chatList.lastIndex).isFromSender) {
                                lifecycleScope.launch {
                                    newMessage.visibility = View.VISIBLE
                                    newMessageTv.text =
                                        chatList.get(chatList.lastIndex).content
                                }
                            }
                        }

                        if (chatList.size > 1) {
                            progressBar.progress = chatList.size
                        }
                        if (progressBar.progress == chatList.size && chatList.size != 0) {
                            progressBar.visibility = View.GONE
                        }

                        if (chatList.isNotEmpty() && check) {
                            lifecycleScope.launch {
                                chatDetailRv.scrollToPosition(
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
                                    chatDetailRv.smoothScrollToPosition(
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
                    val manager = chatDetailRv.layoutManager as LinearLayoutManager

                    manager.apply {
                        val num = findLastVisibleItemPosition()
                        delay(100)
                        chatDetailRv.scrollToPosition(
                            if (num <= 15) 0 else num
                        )
                    }
                }
                false
            }

            newMessage.setOnClickListener {
                newMessage.visibility = View.GONE
                newMessageTv.text = ""

                lifecycleScope.launch {
                    chatViewModel.messageList.value?.let { it1 ->
                        binding.chatDetailRv.smoothScrollToPosition(
                            it1.lastIndex
                        )
                    }
                }
            }

            chatDetailBtn.setOnClickListener {
                if (etSearch.text.toString() != "") {
                    val message = ChatHomeChildDTO(
                        0,
                        chatViewModel.chatRoomNumber,
                        chatViewModel.chatRoomFromID == UserSession.userId,
                        etSearch.text.toString(),
                        false,
                        chatViewModel.timeSetting(Date(System.currentTimeMillis()).toString(), 1)
                    )

                    etSearch.text.clear()

                    chatViewModel.stompSend(message) {}
                }
            }

            chatDetailAdd.setOnClickListener {
                playWebRtc()
            }
        }
    }

    fun loading() {
        lifecycleScope.launch {
            binding.chatDetailRv.visibility = View.GONE
            binding.chatDetailRv.visibility = View.VISIBLE
        }
    }

    private fun playWebRtc() {
        getCameraAndMicPermission {
            videoRepository.sendConnectionRequest("${chatViewModel.chatRoomToID}", true) {
                if (it) {
                    Timber.d("성공적으로 영통 보냄")
                    startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                        putExtra("target", "25")
                        putExtra("isVideoCall", true)
                        putExtra("isCaller", true)
                    })

                }
            }
        }
    }

    override fun onCallReceived(model: DataModel) {
        requireActivity().runOnUiThread {
            binding.apply {
                val isVideoCall = model.type == DataModelType.StartVideoCall
                val isVideoCallText = if (isVideoCall) "Video" else "Audio"
                incomingCallTitleTv.text = "${model.sender} 님이 $isVideoCallText 영상통화를 요청합니다."
                incomingCallLayout.isVisible = true
                acceptButton.setOnClickListener {
                    getCameraAndMicPermission {
                        incomingCallLayout.isVisible = false
                        //create an intent to go to video call activity
                        startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                            putExtra("target", model.sender)
                            putExtra("isVideoCall", isVideoCall)
                            putExtra("isCaller", false)
                        })
                    }
                }
                declineButton.setOnClickListener {
                    incomingCallLayout.isVisible = false
                }
            }
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
        videoServiceRepository.stopService()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}