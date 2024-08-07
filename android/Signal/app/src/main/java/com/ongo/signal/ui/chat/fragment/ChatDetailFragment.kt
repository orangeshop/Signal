package com.ongo.signal.ui.chat.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
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
    private var isKeyboardVisible = false

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

    @SuppressLint("ClickableViewAccessibility", "NewApi")
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
                        **/

                        if (chatViewModel.todayTitleChecker.contains(id)) {
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

                        if (chatList.size >= 1) {
                            progressBar.progress = chatList.size
                            chatViewModel.todayTitleSetting()
                        }
                        if (progressBar.progress == chatList.size && chatList.size != 0) {
                            progressBar.visibility = View.GONE
                        }


                        /** 리스트가 채워져있고 처음이 아니라면 진입
                         *  상대가 메시지를 보낼 시 하단에 미리 알림 띄움
                         * */
                        if (chatList.isNotEmpty() && check == false) {

                            if (isFrom != chatList[chatList.lastIndex].isFromSender && chatDetailRv.canScrollVertically(
                                    1
                                )
                            ) {
                                lifecycleScope.launch {
                                    newMessage.visibility = View.VISIBLE
                                    newMessageTv.text =
                                        chatList[chatList.lastIndex].content
                                }
                            } else if (isFrom != chatList[chatList.lastIndex].isFromSender && !chatDetailRv.canScrollVertically(
                                    1
                                )
                            ) {
                                scrollPositionBottom()
                            }
                        }


                        if (chatList.isNotEmpty() && check) {
                            lifecycleScope.launch {
                                chatDetailRv.scrollToPosition(chatList.lastIndex)
                            }
                            check = false
                        }

                        if (chatList.isNotEmpty() && check == false && chatList.get(chatList.lastIndex).isFromSender == isFrom) {
                            scrollPositionBottom()
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
                    val rootView = root.rootView

                    rootView.viewTreeObserver.addOnGlobalLayoutListener {
                        val rect = Rect()
                        // 루트 뷰의 가시 영역을 rect에 저장합니다.
                        rootView.getWindowVisibleDisplayFrame(rect)
                        val screenHeight = rootView.height
                        val keypadHeight = screenHeight - rect.bottom

                        // 키보드가 화면의 15% 이상을 차지하는 경우 키보드가 올라왔다고 판단합니다.
                        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.35

                        if (isKeyboardNowVisible != isKeyboardVisible) {
                            isKeyboardVisible = isKeyboardNowVisible
                            onKeyboardVisibilityChanged(isKeyboardVisible) {
                            }
                        }
                    }

                    manager.apply {
                        val num = findLastVisibleItemPosition()
                        delay(500)
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

                scrollPositionBottom()
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

    private fun onKeyboardVisibilityChanged(visible: Boolean, onSuccess: () -> Unit) {
        if (visible) {
            // 키보드가 올라왔을 때의 동작
            onSuccess()
            Log.d(TAG, "Keyboard is visible")
        } else {
            // 키보드가 내려갔을 때의 동작
            Log.d(TAG, "Keyboard is hidden")
        }
    }

    fun scrollPositionBottom() {
        lifecycleScope.launch {
            chatViewModel.messageList.value?.let { it1 ->
                binding.chatDetailRv.smoothScrollToPosition(
                    it1.lastIndex
                )
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

