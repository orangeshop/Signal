package com.ongo.signal.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.kakao.sdk.user.model.User
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.databinding.FragmentMatchReviewBinding
import com.ongo.signal.ui.main.viewmodel.ReviewViewModel
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.util.STTHelper
import com.ongo.signal.util.tierSetting
import timber.log.Timber

class MatchReviewFragment :
    BaseFragment<FragmentMatchReviewBinding>(R.layout.fragment_match_review) {

    private lateinit var sttHelper: STTHelper
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private var writerId: Long = 0

    override fun init() {
        binding.fragment = this

        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    binding.tietId.setText(recognizedText)
                }
            }
        sttHelper = STTHelper(sttLauncher)
        val safeArgs: MatchReviewFragmentArgs by navArgs()

        if (safeArgs.flagByRootReview) {
            writerId = safeArgs.flagByRootId
        }
        getMyProfile(writerId)
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

    fun clickSubmit() {
        val rating = binding.rbRating.rating.toInt()
        val content = binding.tietId.text.toString()
        val userId = UserSession.userId

        if (userId != null && writerId != 0L) {
            val reviewRequest = ReviewRequestDTO(
                userId = writerId,
                content = content,
                writerId = userId,
                star = rating
            )

            Timber.tag("writerId").d("writerId: $writerId")
            reviewViewModel.writeReview(reviewRequest)

            setFragmentResult("reviewSubmitted", Bundle())
            findNavController().popBackStack()
        } else {
            makeToast("리뷰 작성에 실패했습니다.")
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}