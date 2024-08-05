package com.ongo.signal.ui.main.fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.FragmentMainBinding
import com.ongo.signal.ui.main.adapter.TagAdapter
import com.ongo.signal.ui.main.adapter.TodayPostAdapter
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.util.KeyboardUtils
import com.ongo.signal.util.STTHelper
import com.ongo.signal.util.SpannableStringUtils
import com.ongo.signal.util.TTSHelper
import com.ongo.signal.util.ViewAnimation.fadeIn
import com.ongo.signal.util.ViewAnimation.fadeOut
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val boardViewModel: BoardViewModel by activityViewModels()
    private lateinit var todayPostAdapter: TodayPostAdapter
    private lateinit var firstTagAdapter: TagAdapter
    private lateinit var secondTagAdapter: TagAdapter
    private lateinit var thirdTagAdapter: TagAdapter
    private lateinit var ttsHelper: TTSHelper
    private lateinit var sttHelper: STTHelper
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private val handler = Handler(Looper.getMainLooper())
    private val flipInterval = 3000L

    override fun init() {
        binding.fragment = this
        binding.boardViewModel = boardViewModel
        ttsHelper = TTSHelper(requireContext())

        setUpAdapter()
        setUpSpannableText()

        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    binding?.etSearch?.setText(recognizedText)
                }
            }

        sttHelper = STTHelper(sttLauncher)

        observeBoards()
        observeHotSignalBoards()

        startFlippingViews()
    }

    private fun observeBoards() {
        lifecycleScope.launch {
            boardViewModel.items.collectLatest { newBoards ->
                Timber.d("New boards received: $newBoards")
                todayPostAdapter.submitData(newBoards)
//                updateTagAdapters(newBoards)
            }
        }
    }

    private fun observeHotSignalBoards() {
        lifecycleScope.launch {
            boardViewModel.hotBoards.collectLatest { newHotBoards ->
                Timber.d("MainFragment - New hot signal boards received: $newHotBoards")
                if (isAdded && view != null) {
                    updateHotSignalViews(newHotBoards)
                }
            }
        }
    }

    private fun updateHotSignalViews(newHotBoards: List<BoardDTO>) {
        val binding = binding ?: return

        when (newHotBoards.size) {
            0 -> {
                binding.tvFirst.visibility = View.GONE
                binding.tvFirstTitle.visibility = View.GONE
                binding.rvFirst.visibility = View.GONE
                binding.tvSecond.visibility = View.GONE
                binding.tvSecondTitle.visibility = View.GONE
                binding.rvSecond.visibility = View.GONE
                binding.tvThird.visibility = View.GONE
                binding.tvThirdTitle.visibility = View.GONE
                binding.rvThird.visibility = View.GONE
            }

            1 -> {
                binding.tvFirst.visibility = View.VISIBLE
                binding.tvFirstTitle.visibility = View.VISIBLE
                binding.rvFirst.visibility = View.VISIBLE
                binding.tvSecond.visibility = View.GONE
                binding.tvSecondTitle.visibility = View.GONE
                binding.rvSecond.visibility = View.GONE
                binding.tvThird.visibility = View.GONE
                binding.tvThirdTitle.visibility = View.GONE
                binding.rvThird.visibility = View.GONE
            }

            2 -> {
                binding.tvFirst.visibility = View.VISIBLE
                binding.tvFirstTitle.visibility = View.VISIBLE
                binding.rvFirst.visibility = View.VISIBLE
                binding.tvSecond.visibility = View.VISIBLE
                binding.tvSecondTitle.visibility = View.VISIBLE
                binding.rvSecond.visibility = View.VISIBLE
                binding.tvThird.visibility = View.GONE
                binding.tvThirdTitle.visibility = View.GONE
                binding.rvThird.visibility = View.GONE
            }

            else -> {
                binding.tvFirst.visibility = View.VISIBLE
                binding.tvFirstTitle.visibility = View.VISIBLE
                binding.rvFirst.visibility = View.VISIBLE
                binding.tvSecond.visibility = View.VISIBLE
                binding.tvSecondTitle.visibility = View.VISIBLE
                binding.rvSecond.visibility = View.VISIBLE
                binding.tvThird.visibility = View.VISIBLE
                binding.tvThirdTitle.visibility = View.VISIBLE
                binding.rvThird.visibility = View.VISIBLE
                updateHotSignalTitles(newHotBoards)
                updateTagAdapters(newHotBoards)
            }
        }

        updateHotSignalTitles(newHotBoards)
        updateTagAdapters(newHotBoards)
    }


    private fun updateHotSignalTitles(newHotBoards: List<BoardDTO>) {
        binding?.let {
            it.tvFirstTitle.text = newHotBoards.getOrNull(0)?.title ?: ""
            it.tvSecondTitle.text = newHotBoards.getOrNull(1)?.title ?: ""
            it.tvThirdTitle.text = newHotBoards.getOrNull(2)?.title ?: ""
        }
    }

    private fun updateTagAdapters(newBoards: List<BoardDTO>) {
        if (newBoards.isNotEmpty()) {
            val firstBoardTags = newBoards.getOrNull(0)?.tags ?: emptyList()
            Timber.d("First board tags: $firstBoardTags")
            val secondBoardTags = newBoards.getOrNull(1)?.tags ?: emptyList()
            secondTagAdapter.submitList(secondBoardTags)
            val thirdBoardTags = newBoards.getOrNull(2)?.tags ?: emptyList()
            thirdTagAdapter.submitList(thirdBoardTags)
        } else {
            firstTagAdapter.submitList(emptyList())
            secondTagAdapter.submitList(emptyList())
            thirdTagAdapter.submitList(emptyList())
        }
    }


    fun onChipClicked(tag: String) {
        if (boardViewModel.selectedTag.value == tag) {
            boardViewModel.clearBoards()
        } else {
            boardViewModel.setSelectedTag(tag)
        }
    }

    private fun startFlippingViews() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isAdded && view != null) {
                    flipViews()
                }

                handler.postDelayed(this, flipInterval)
            }
        }, flipInterval)
    }

    private fun flipViews() {
        binding?.let {
            flipView(it.tvFirstTitle, it.rvFirst, 0)
            flipView(it.tvSecondTitle, it.rvSecond, 1)
            flipView(it.tvThirdTitle, it.rvThird, 2)
        }

    }

    private fun flipView(titleView: View, recyclerView: RecyclerView, position: Int) {
        val context = titleView.context
        val flipOut = AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet
        val flipIn = AnimatorInflater.loadAnimator(context, R.animator.flip_in) as AnimatorSet
        val flipOutRecycler =
            AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet
        val flipInRecycler =
            AnimatorInflater.loadAnimator(context, R.animator.flip_in) as AnimatorSet

        flipOut.setTarget(titleView)
        flipOutRecycler.setTarget(recyclerView)

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (boardViewModel.hotBoards.value.isNotEmpty() && isAdded && view != null) {
                    val newPosition = position % boardViewModel.hotBoards.value.size
                    titleView.tag = newPosition
                    (titleView as TextView).text = boardViewModel.hotBoards.value[newPosition].title

                    flipIn.setTarget(titleView)
                    flipInRecycler.setTarget(recyclerView)
                    flipIn.start()
                    flipInRecycler.start()
                }
            }
        })

        flipOut.start()
        flipOutRecycler.start()
    }

    fun onFABClicked() {
        boardViewModel.clearBoard()
        findNavController().navigate(R.id.action_mainFragment_to_writePostFragment)
    }

    fun onMicClicked() {
        sttHelper.startSpeechToText()
    }

    fun onTitleClicked(position: Int) {
        val board = boardViewModel.hotBoards.value.getOrNull(position)
        board?.let {
            boardViewModel.selectBoard(it)
            findNavController().navigate(R.id.action_mainFragment_to_postFragment)
        }
    }

    fun searchKeyword() {
        val keyword = binding?.etSearch?.text.toString()
        boardViewModel.searchBoard(keyword)
        binding?.etSearch?.let { KeyboardUtils.hideKeyboard(it) }
        fadeIn(binding?.ivSearch)
        fadeIn(binding?.ivMic)
        fadeOut(binding?.ivRefresh)
    }

    fun loadAllBoards() {
        boardViewModel.clearBoards()
        binding?.etSearch?.setText("")
        binding?.etSearch?.let { KeyboardUtils.hideKeyboard(it) }
        binding?.chipGroup?.clearCheck()
        fadeIn(binding?.ivSearch)
        fadeIn(binding?.ivMic)
        fadeOut(binding?.ivRefresh)
    }

    private fun fadeIn(view: View?) {
        view?.let {
            fadeIn(it, requireContext())
        }
    }


    private fun fadeOut(view: View?) {
        view?.let {
            fadeOut(it, requireContext())
        }
    }

    private fun setUpAdapter() {
        todayPostAdapter = TodayPostAdapter(
            onItemClicked = { board ->
                boardViewModel.selectBoard(board)
                findNavController().navigate(R.id.action_mainFragment_to_postFragment)
            },
            onTTSClicked = { content ->
                ttsHelper.speak(content)
            },
            viewModel = boardViewModel
        )

        firstTagAdapter = TagAdapter()
        binding?.rvFirst?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = firstTagAdapter
        }

        secondTagAdapter = TagAdapter()
        binding?.rvSecond?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = secondTagAdapter
        }

        thirdTagAdapter = TagAdapter()
        binding?.rvThird?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = thirdTagAdapter
        }

        binding?.rvPost?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayPostAdapter
        }
    }

    private fun setUpSpannableText() {
        val hotSpannable = SpannableStringUtils.getSpannableString(
            getString(R.string.hotSignal),
            getString(R.string.signal),
            "#64FFCE"
        )
        val todaySpannable = SpannableStringUtils.getSpannableString(
            getString(R.string.realtime_signal),
            getString(R.string.signal),
            "#64FFCE"
        )


        binding?.tvHotSignal?.text = hotSpannable
        binding?.tvTodaySignal?.text = todaySpannable
    }

    override fun onDestroyView() {
        ttsHelper.shutdown()
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}
