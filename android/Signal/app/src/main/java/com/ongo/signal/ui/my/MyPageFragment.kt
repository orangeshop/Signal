package com.ongo.signal.ui.my

import android.content.Intent
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.databinding.FragmentMypageBinding
import com.ongo.signal.ui.LoginActivity
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(R.layout.fragment_mypage) {

    private val viewModel: MyPageViewModel by viewModels()

    override fun init() {
        initViews()
        binding.fragment = this
    }

    private fun initViews() {
        binding.ivLogout.setOnClickListener {
            UserSession.refreshToken?.let { refreshToken ->
                viewModel.sendLogout(refreshToken) { successFlag ->
                    if (successFlag == 1) {
                        makeToast("로그아웃 되었습니다.")
                        goToLoginActivity()
                    }
                }
            }
        }
        binding.tvLogout.setOnClickListener {
            UserSession.refreshToken?.let { refreshToken ->
                viewModel.sendLogout(refreshToken) { successFlag ->
                    if (successFlag == 1) {
                        makeToast("로그아웃 되었습니다.")
                        goToLoginActivity()
                    }
                }
            }
        }
    }

    fun goToProfileEdit() {
        parentFragmentManager.commit {
            (requireActivity() as MainActivity).hideBottomNavigation()
            findNavController().navigate(R.id.action_myPageFragment_to_profileEditFragment)
        }
    }

    fun goToMySignal() {
        parentFragmentManager.commit {
            (requireActivity() as MainActivity).hideBottomNavigation()
            findNavController().navigate(R.id.action_myPageFragment_to_mySignalFragment)
        }
    }

    fun goToMyCommentSignal() {
        parentFragmentManager.commit {
            (requireActivity() as MainActivity).hideBottomNavigation()
            findNavController().navigate(R.id.action_myPageFragment_to_myCommentSignalFragment)
        }
    }

    fun goToReview() {
        parentFragmentManager.commit {
            (requireActivity() as MainActivity).hideBottomNavigation()
            findNavController().navigate(R.id.action_myPageFragment_to_reviewFragment)
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}