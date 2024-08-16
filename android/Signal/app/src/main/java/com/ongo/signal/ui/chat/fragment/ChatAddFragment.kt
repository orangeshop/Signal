package com.ongo.signal.ui.chat.fragment

import android.os.Build.VERSION_CODES.P
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.FragmentChatAddBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Date

private const val TAG = "ChatAddFragment_싸피"
@AndroidEntryPoint
class ChatAddFragment : BaseFragment<FragmentChatAddBinding>(R.layout.fragment_chat_add) {
    override fun init() {
        TODO("Not yet implemented")
    }

//    private lateinit var topAdapter: ChatAddTopAdapter
//    private lateinit var profileAdapter: ChatAddProfileAdapter
//    private val chatViewModel : ChatHomeViewModel by activityViewModels()
//    private val chatAddViewModel : ChatAddViewModel by viewModels()
//
//    override fun init() {
//        (requireActivity() as? MainActivity)?.hideBottomNavigation()
//        binding.apply {
//
//
//
//            topAdapter = ChatAddTopAdapter()
//            profileAdapter = ChatAddProfileAdapter()
//
//            binding.chatAddTopRv.adapter = topAdapter
//            binding.chatAddProfileRv.adapter = profileAdapter
//
//            lifecycleOwner?.let {
//                chatAddViewModel.topList.observe(it) { chatTopList ->
//                    topAdapter.submitList(chatTopList)
//                }
//            }
//
//            lifecycleOwner?.let {
//                chatAddViewModel.profileList.observe(it) { chatProfileList ->
//                    profileAdapter.submitList(chatProfileList)
//                }
//            }
//
//            binding.chatAddBtn.setOnClickListener {
////                chatViewModel.saveChat(
////                    ChatHomeDTO(
////                        0, 1, 2, "last", "null", Date(System.currentTimeMillis()).toString()
////                    )
////                )
//
//
//
//                findNavController().popBackStack()
//                Log.d(TAG, "init: ${findNavController().previousBackStackEntry}")
//            }
//
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}