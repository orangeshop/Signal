package com.ongo.signal.ui.main.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.CreateChatRoom
import com.ongo.signal.config.UserSession
import com.ongo.signal.databinding.FragmentReviewBinding
import com.ongo.signal.ui.main.ReviewViewModel
import com.ongo.signal.ui.main.adapter.ReviewAdapter
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding>(R.layout.fragment_review) {

    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()

    override fun init() {
        setUpAdapter()
        binding.fragment = this
        binding.reviewViewModel = reviewViewModel

        //user ID에 상대방 아이디를 넣으면 됩니다.
        //나중에 프로필을 클릭한 상대의 userId가 들어가도록 수정
        val writerId = boardViewModel.selectedBoard.value?.userId
        val writerName = boardViewModel.selectedBoard.value?.writer
        writerId?.let {
            reviewViewModel.checkReviewPermission(writerId)
            getMyProfile(writerId)
        }

        binding.btnMatching.setOnClickListener {
            Timber.d("매칭 클릭 확인 ${writerId} ${writerName}")
            UserSession.userId?.let { myId ->
                writerId?.let {
                    writerName?.let {
                        reviewViewModel.postProposeMatch(
                            fromId = myId,
                            toId = writerId
                        ) {
                            makeToast("${writerName} 님께 매칭 신청을 하였습니다.")
                        }
                    }
                }
            }
        }

        loadReviews()
    }

    fun makeChat() {
        val writerId = boardViewModel.selectedBoard.value?.userId
        val userId = UserSession.userId

        if (userId != null) {
            if (writerId != null) {
                CreateChatRoom.Create(userId, writerId)
                findNavController().navigate(R.id.action_reviewFragment_to_chatFragment)
            } else {
                Timber.d("writerId is null")
            }
        } else {
            Timber.d("userId is null")
        }
    }

    private fun loadReviews() {
        val writerId = boardViewModel.selectedBoard.value?.userId ?: UserSession.userId
        lifecycleScope.launch {
            writerId?.let {
                reviewViewModel.loadReview(writerId)
            }

            reviewViewModel.reviewList.collectLatest { review ->
                reviewAdapter.submitList(review)
            }
        }
    }

    private fun getMyProfile(userId: Long) {
        reviewViewModel.getUserProfile(userId) { userProfileResponse ->
            if (userProfileResponse.profileImage.isBlank()) {
                binding.ivProfile.setImageResource(R.drawable.basic_profile)
            } else {
                Glide.with(requireActivity())
                    .load(userProfileResponse.profileImage)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.ivProfile)
            }

            binding.tvUsername.text = userProfileResponse.name
        }
    }

    private fun setUpAdapter() {
        reviewAdapter = ReviewAdapter()
        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = reviewAdapter
        }
    }

    fun onReview() {
        findNavController().navigate(R.id.action_reviewFragment_to_matchReviewFragment)
    }
}