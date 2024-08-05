package com.ongo.signal.ui.main.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
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
        writerId?.let {
            reviewViewModel.checkReviewPermission(25)
        }

        loadReviews()
    }

    private fun loadReviews() {
        lifecycleScope.launch {
            reviewViewModel.loadReview(8)

            reviewViewModel.reviewList.collectLatest { review ->
                reviewAdapter.submitList(review)
            }
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