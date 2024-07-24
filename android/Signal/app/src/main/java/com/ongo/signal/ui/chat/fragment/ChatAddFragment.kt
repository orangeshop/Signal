package com.ongo.signal.ui.chat.fragment

import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentChatAddBinding
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatAddFragment : BaseFragment<FragmentChatAddBinding>(R.layout.fragment_chat_add) {
    override fun init() {
        (requireActivity() as? MainActivity)?.hideBottomNavigation()
        binding.apply {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.showBottomNavigation()
    }
}