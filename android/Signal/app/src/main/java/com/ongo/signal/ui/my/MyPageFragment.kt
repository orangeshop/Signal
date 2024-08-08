package com.ongo.signal.ui.my

import android.content.Intent
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
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

    override fun onResume() {
        super.onResume()
        getMyProfile()
    }

    private fun getMyProfile() {
        UserSession.accessToken?.let {
            viewModel.getMyProfile(it) { myProfileData ->
                Timber.d("프로필 받아옴 ${myProfileData}")
                with(binding) {
                    if (myProfileData.profileImage == "null") {
                        ivProfile.setImageResource(R.drawable.basic_profile)
                    } else {
                        //TODO place홀더 로딩 이미지 찾아보기
                        binding.ivProfile.visibility = View.VISIBLE
                        binding.pbLoading.visibility = View.GONE
                        if (myProfileData.profileImage.isBlank()) {
                            ivProfile.setImageResource(R.drawable.basic_profile)
                        } else {
                            Glide.with(requireActivity())
                                .load(myProfileData.profileImage)
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(ivProfile)
                        }
                    }

                    tvUsername.text = myProfileData.name
                }
            }
        }
    }

    private fun initViews() {
        binding.ivLogout.setOnClickListener {
            UserSession.refreshToken?.let { refreshToken ->
                UserSession.accessToken?.let { accessToken ->
                    viewModel.sendLogout(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    ) { successFlag ->
                        if (successFlag == 1) {
                            makeToast("로그아웃 되었습니다.")
                            goToLoginActivity()
                        }
                    }
                }
            }
        }
        binding.tvLogout.setOnClickListener {
            UserSession.refreshToken?.let { refreshToken ->
                UserSession.accessToken?.let { accessToken ->
                    viewModel.sendLogout(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    ) { successFlag ->
                        if (successFlag == 1) {
                            makeToast("로그아웃 되었습니다.")
                            goToLoginActivity()
                        }
                    }
                }
            }
        }
    }

    fun goToProfileEdit() {
        parentFragmentManager.commit {
            (requireActivity() as MainActivity).hideBottomNavigation()
            Timber.d("주기전 데이터 확인 ${viewModel.userData}")
            findNavController().navigate(
                MyPageFragmentDirections.actionMyPageFragmentToProfileEditFragment(
                    profileData = viewModel.userData
                )
            )
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