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
import com.ongo.signal.data.model.chat.ChatAddListDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.FragmentChatAddBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.chat.viewmodels.ChatHomeViewModel
import com.ongo.signal.ui.chat.adapter.ChatAddProfileAdapter
import com.ongo.signal.ui.chat.adapter.ChatAddTopAdapter
import com.ongo.signal.ui.chat.viewmodels.ChatAddViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChatAddFragment_싸피"
@AndroidEntryPoint
class ChatAddFragment : BaseFragment<FragmentChatAddBinding>(R.layout.fragment_chat_add) {

    private lateinit var topAdapter: ChatAddTopAdapter
    private lateinit var profileAdapter: ChatAddProfileAdapter
    private val chatViewModel : ChatHomeViewModel by activityViewModels()
    private val chatAddViewModel : ChatAddViewModel by viewModels()

    override fun init() {
        (requireActivity() as? MainActivity)?.hideBottomNavigation()
        binding.apply {



            topAdapter = ChatAddTopAdapter()
            profileAdapter = ChatAddProfileAdapter()

            binding.chatAddTopRv.adapter = topAdapter
            binding.chatAddProfileRv.adapter = profileAdapter

            lifecycleOwner?.let {
                chatAddViewModel.topList.observe(it) { chatTopList ->
                    topAdapter.submitList(chatTopList)
                }
            }

            lifecycleOwner?.let {
                chatAddViewModel.profileList.observe(it) { chatProfileList ->
                    profileAdapter.submitList(chatProfileList)
                }
            }

            val test = ChatHomeDTO(
                0, 1, 2, "last", "status"
            )

            binding.chatAddBtn.setOnClickListener {
                chatViewModel.saveChat(
                    test
                )



                findNavController().popBackStack()
                Log.d(TAG, "init: ${findNavController().previousBackStackEntry}")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}