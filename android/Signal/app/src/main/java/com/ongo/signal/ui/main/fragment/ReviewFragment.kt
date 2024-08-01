package com.ongo.signal.ui.main.fragment

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.main.ReviewDTO
import com.ongo.signal.databinding.FragmentReviewBinding
import com.ongo.signal.ui.main.adapter.ReviewAdapter

class ReviewFragment : BaseFragment<FragmentReviewBinding>(R.layout.fragment_review) {

    private lateinit var reviewAdapter: ReviewAdapter

    override fun init() {
        setUpAdapter()
        populateReviewData()
        binding.fragment = this
    }

    private fun setUpAdapter() {
        reviewAdapter = ReviewAdapter()
        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = reviewAdapter
        }
    }

    private fun populateReviewData() {
        val sampleReviews = listOf(
            ReviewDTO(
                reviewId = "1",
                userName = "이름이름1",
                review = "리뷰 내용1",
                rating = 4.5f,
                profile = "https://example.com/profile1.jpg"
            ),
            ReviewDTO(
                reviewId = "2",
                userName = "이름이름2",
                review = "리뷰 내용2",
                rating = 3.0f,
                profile = "https://example.com/profile2.jpg"
            ),
            ReviewDTO(
                reviewId = "3",
                userName = "이름이름3",
                review = "리뷰 내용3",
                rating = 5.0f,
                profile = "https://example.com/profile3.jpg"
            )
        )
        reviewAdapter.submitList(sampleReviews)
    }

    fun onReview() {
        findNavController().navigate(R.id.action_reviewFragment_to_matchReviewFragment)
    }
}