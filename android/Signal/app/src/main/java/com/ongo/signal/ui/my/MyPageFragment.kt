package com.ongo.signal.ui.my

import android.content.Intent
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMypageBinding
import com.ongo.signal.ui.LoginActivity
import com.ongo.signal.ui.MainActivity
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

        binding.tvLogout.setOnClickListener {
            goToLoginActivity()
        }
        binding.tvLogout.setOnClickListener {
            goToLoginActivity()
        }
    }

    private fun goToProfileEdit() {
        parentFragmentManager.commit {
            (requireActivity() as MainActivity).hideBottomNavigation()
            findNavController().navigate(R.id.action_myPageFragment_to_profileEditFragment)
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}