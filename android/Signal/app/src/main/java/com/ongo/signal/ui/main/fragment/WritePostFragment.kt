package com.ongo.signal.ui.main.fragment

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentWritePostBinding
import com.ongo.signal.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class WritePostFragment : BaseFragment<FragmentWritePostBinding>(R.layout.fragment_write_post) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private var currentTarget: Int = 0

    override fun init() {
        binding.fragment = this

        sttLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val speechResults = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val recognizedText = speechResults?.get(0).toString()
                when (currentTarget) {
                    R.id.iv_title_mic -> binding.etTitle.setText(recognizedText)
                    R.id.iv_content_mic -> binding.etContent.setText(recognizedText)
                }
            }
        }

    }

    fun setupListeners() {
        binding.ivTitleMic.setOnClickListener {
            currentTarget = R.id.iv_title_mic
            startSpeechToText()
        }

        binding.ivContentMic.setOnClickListener {
            currentTarget = R.id.iv_content_mic
            startSpeechToText()
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "검색어를 말해주세요.")
        }
        sttLauncher.launch(intent)
    }

    fun onRegisterButtonClick() {
        findNavController().navigate(R.id.action_writePostFragment_to_postFragment)
    }
}