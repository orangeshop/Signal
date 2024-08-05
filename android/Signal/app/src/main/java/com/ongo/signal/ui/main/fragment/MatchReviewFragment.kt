package com.ongo.signal.ui.main.fragment

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.databinding.FragmentMatchReviewBinding
import com.ongo.signal.ui.main.ReviewViewModel
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.util.STTHelper

class MatchReviewFragment :
    BaseFragment<FragmentMatchReviewBinding>(R.layout.fragment_match_review) {

    private lateinit var sttHelper: STTHelper
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()

    override fun init() {
        binding.fragment = this

        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    binding.tietId.setText(recognizedText)
                }
            }
        sttHelper = STTHelper(sttLauncher)
    }

    fun clickSubmit() {
        val rating = binding.rbRating.rating.toInt()
        val content = binding.tietId.text.toString()
        val userId = 24L
        val writerId = boardViewModel.selectedBoard.value?.userId

        writerId?.let {
            ReviewRequestDTO(
                userId = userId,
                content = content,
                writerId = 25,
                star = rating
            )
        }?.let {
            reviewViewModel.writeReview(
                it
            )
        }

        findNavController().popBackStack()
    }
}