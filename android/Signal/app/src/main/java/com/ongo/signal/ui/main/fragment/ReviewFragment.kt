package com.ongo.signal.ui.main.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.CreateChatRoom
import com.ongo.signal.config.UserSession
import com.ongo.signal.databinding.FragmentReviewBinding
import com.ongo.signal.ui.main.viewmodel.ReviewViewModel
import com.ongo.signal.ui.main.adapter.ReviewAdapter
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.util.tierSetting
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
        val safeArgs: ReviewFragmentArgs by navArgs()

        var writerId = boardViewModel.selectedBoard.value?.userId
        var writerName = boardViewModel.selectedBoard.value?.writer

        if(safeArgs.flagByRoot == true){

            binding.btnChat.visibility = View.GONE
            writerId = safeArgs.flagByRootId
            writerName = safeArgs.flagByRootWriter
        }

        if(UserSession.userId == writerId){
            binding.btnChat.visibility = View.GONE
        }

        //user ID에 상대방 아이디를 넣으면 됩니다.
        //나중에 프로필을 클릭한 상대의 userId가 들어가도록 수정

        writerId?.let {
            reviewViewModel.checkReviewPermission(writerId)
            getMyProfile(writerId)
        }

        loadReviews()
    }

    fun makeChat() {
        val writerId = boardViewModel.selectedBoard.value?.userId
        val userId = UserSession.userId

        if (userId != null) {
            if (writerId != null) {
                CreateChatRoom.Create(userId, writerId)

                findNavController().navigate(R.id.chatFragment, null, navOptions = NavOptions.Builder().setPopUpTo(findNavController().graph.startDestinationId, true).build())

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
            val tierImageRes = tierSetting(userProfileResponse.score)
            binding.ivUserTier.setImageResource(tierImageRes)
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