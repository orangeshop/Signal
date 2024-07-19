package com.ongo.signal.ui.chat.fragment

import android.os.Bundle
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentChatBinding
import com.ongo.signal.databinding.FragmentChatDetailBinding
import com.ongo.signal.ui.MainActivity

class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding>(R.layout.fragment_chat_detail) {

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