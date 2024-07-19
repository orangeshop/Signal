package com.ongo.signal.ui.chat.fragment

import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentChatBinding
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatAddFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat_add) {
    override fun init() {
        (activity as? MainActivity)?.hideBottomNavigationView()
        binding.apply {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? MainActivity)?.showBottomNavigationView()
    }
}