package com.ongo.signal.ui.main.fragment

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMatchReviewBinding
import com.ongo.signal.util.STTHelper

class MatchReviewFragment :
    BaseFragment<FragmentMatchReviewBinding>(R.layout.fragment_match_review) {

    private lateinit var sttHelper: STTHelper
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>

    override fun init() {
        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    binding.tietId.setText(recognizedText)
                }
            }
        sttHelper = STTHelper(sttLauncher)
        binding.fragment = this
    }

    fun clickSubmit() {
        val rating = binding.rbRating.rating
        val review = binding.tietId.text.toString()
    }
}