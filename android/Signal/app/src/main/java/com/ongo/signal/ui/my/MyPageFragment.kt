package com.ongo.signal.ui.my

import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMypageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(R.layout.fragment_mypage) {
    override fun init() {
        initViews()
    }

    private fun initViews() {
        binding.ivProfileIcon.setOnClickListener {
            goToProfileEdit()
        }
        binding.tvEditProfile.setOnClickListener {
            goToProfileEdit()
        }
    }

    private fun goToProfileEdit() {
        parentFragmentManager.commit {
            findNavController().navigate(R.id.action_myPageFragment_to_profileEditFragment)
        }
    }
}