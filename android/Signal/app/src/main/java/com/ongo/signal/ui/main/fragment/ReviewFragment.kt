package com.ongo.signal.ui.main.fragment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
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
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeCreateDTO
import com.ongo.signal.data.repository.chat.chatservice.ChatRepositoryImpl
import com.ongo.signal.databinding.FragmentReviewBinding
import com.ongo.signal.ui.main.adapter.ReviewAdapter
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.ui.main.viewmodel.ReviewViewModel
import com.ongo.signal.util.tierSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding>(R.layout.fragment_review) {

    @Inject
    lateinit var chatRepositoryImpl: ChatRepositoryImpl
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private var writerId: Long? = 0L

    override fun init() {
        setUpAdapter()
        binding.fragment = this
        binding.reviewViewModel = reviewViewModel
        val safeArgs: ReviewFragmentArgs by navArgs()
        writerId = boardViewModel.selectedBoard.value?.userId ?: UserSession.userId

        if (safeArgs.flagByRoot) {
            binding.btnChat.visibility = View.GONE
            writerId = safeArgs.flagByRootId
        }
        writerId?.let { getMyProfile(it) }
        writerId?.let {
            reviewViewModel.checkReviewPermission(it)
            reviewViewModel.loadReview(it)
        }

        if (UserSession.userId == writerId) {
            binding.btnChat.visibility = View.GONE
        }

        observePermission()
        setFragmentResultListener("reviewSubmitted") { _, _ ->
            loadReviews()
        }
    }

    private fun observePermission() {
        viewLifecycleOwner.lifecycleScope.launch {
            reviewViewModel.canWriteReview.collectLatest { canWriteReview ->
                Timber.d("canWriteReview: $canWriteReview")
                Timber.d("canVisible: ${reviewViewModel.isReviewVisible.value}")
                binding.ivReview.visibility = if (canWriteReview) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadReviews()
    }

    fun makeChat() {
        val userId = UserSession.userId

        if (userId != null) {
            if (writerId != null) {
                lifecycleScope.launch {
                    chatRepositoryImpl.saveChatRoom(
                        ChatHomeCreateDTO(
                            fromId = userId,
                            toId = writerId!!
                        )
                    )
                }
                findNavController().navigate(
                    R.id.chatFragment,
                    null,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(findNavController().graph.startDestinationId, true).build()
                )

            } else {
                Timber.d("writerId is null")
            }
        } else {
            Timber.d("userId is null")
        }
    }

    private fun loadReviews() {
        lifecycleScope.launch {
            Timber.tag("writerId").d("로드 할 writerId: $writerId")
            writerId?.let {
                reviewViewModel.loadReview(writerId!!)
            }

            reviewViewModel.reviewList.collectLatest { review ->
                Timber.tag("writerId").d("reviews: $review")
                reviewAdapter.submitList(review)
            }
        }
    }

    private fun getMyProfile(userId: Long) {
        Timber.tag("reviewId").d("profile 가져 올 writerId: $userId")
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
        findNavController().navigate(
            ReviewFragmentDirections
                .actionReviewFragmentToMatchReviewFragment(
                    flagByRootReview = true,
                    flagByRootId = writerId!!,
                    flagByName = binding.tvUsername.text.toString()
                )
        )
    }
}