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
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.FragmentMainBinding
import com.ongo.signal.ui.main.MainViewModel
import com.ongo.signal.ui.main.adapter.TagAdapter
import com.ongo.signal.ui.main.adapter.TodayPostAdapter
import com.ongo.signal.util.STTHelper
import com.ongo.signal.util.TTSHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by activityViewModels()
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
        binding.viewModel = viewModel
        ttsHelper = TTSHelper(requireContext())

        setUpAdapter()
        setUpSpannableText()

        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    binding.etSearch.setText(recognizedText)
                }
            }

        sttHelper = STTHelper(sttLauncher)

        observeBoards()
        observeHotSignalBoards()

        startFlippingViews()
    }

    private fun observeBoards() {
        lifecycleScope.launch {
            viewModel.boards.collectLatest { newBoards ->
                Timber.d("New boards received: $newBoards")
                todayPostAdapter.submitList(newBoards)
//                updateTagAdapters(newBoards)
            }
        }
    }

    private fun observeHotSignalBoards() {
        lifecycleScope.launch {
            viewModel.hotSignalBoards.collectLatest { newHotBoards ->
                Timber.d("New hot signal boards received: $newHotBoards")
                if (newHotBoards.isEmpty()) {
//                    binding.tvFirst.visibility = View.GONE
//                    binding.tvFirstTitle.visibility = View.GONE
//                    binding.rvFirst.visibility = View.GONE
//                    binding.tvSecond.visibility = View.GONE
//                    binding.tvSecondTitle.visibility = View.GONE
//                    binding.rvSecond.visibility = View.GONE
//                    binding.tvThird.visibility = View.GONE
//                    binding.tvThirdTitle.visibility = View.GONE
//                    binding.rvThird.visibility = View.GONE
//                    binding.tvEmptyMessage.visibility = View.VISIBLE
//                    updateTagAdapters(emptyList())
                } else {
//                    binding.tvFirst.visibility = View.VISIBLE
//                    binding.tvFirstTitle.visibility = View.VISIBLE
//                    binding.rvFirst.visibility = View.VISIBLE
//                    binding.tvSecond.visibility = View.VISIBLE
//                    binding.tvSecondTitle.visibility = View.VISIBLE
//                    binding.rvSecond.visibility = View.VISIBLE
//                    binding.tvThird.visibility = View.VISIBLE
//                    binding.tvThirdTitle.visibility = View.VISIBLE
//                    binding.rvThird.visibility = View.VISIBLE
//                    binding.tvEmptyMessage.visibility = View.GONE
//
//                    updateHotSignalTitles(newHotBoards)
//                    updateTagAdapters(newHotBoards)
                }
            }
        }
    }

    private fun updateHotSignalTitles(newHotBoards: List<BoardDTO>) {
//        binding.tvFirstTitle.text = newHotBoards.getOrNull(0)?.title ?: ""
//        binding.tvSecondTitle.text = newHotBoards.getOrNull(1)?.title ?: ""
//        binding.tvThirdTitle.text = newHotBoards.getOrNull(2)?.title ?: ""
    }


    private fun updateTagAdapters(newBoards: List<BoardDTO>) {
        if (newBoards.isNotEmpty()) {
            val firstBoardTags = newBoards.getOrNull(0)?.tags ?: emptyList()
            Timber.d("First board tags: $firstBoardTags")
            firstTagAdapter.submitList(firstBoardTags)
            val secondBoardTags = newBoards.getOrNull(1)?.tags ?: emptyList()
            Timber.d("Second board tags: $secondBoardTags")
            secondTagAdapter.submitList(secondBoardTags)
            val thirdBoardTags = newBoards.getOrNull(2)?.tags ?: emptyList()
            Timber.d("Third board tags: $thirdBoardTags")
            thirdTagAdapter.submitList(thirdBoardTags)
        } else {
            firstTagAdapter.submitList(emptyList())
            secondTagAdapter.submitList(emptyList())
            thirdTagAdapter.submitList(emptyList())
        }
    }


    fun onChipClicked(tag: String) {
        if (viewModel.selectedTag.value == tag) {
            viewModel.clearSelectedTag()
            viewModel.clearSearch()
            viewModel.loadHotSignalBoards()
        } else {
            viewModel.clearSelectedTag()
            viewModel.setSelectedTag(tag)
            viewModel.loadHotAndRecentSignalBoardsByTag(tag, 0, 10)
        }
    }

    private fun startFlippingViews() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                flipViews()
                handler.postDelayed(this, flipInterval)
            }
        }, flipInterval)
    }

    private fun flipViews() {
        flipView(binding.tvFirstTitle, binding.rvFirst, 0)
        flipView(binding.tvSecondTitle, binding.rvSecond, 1)
        flipView(binding.tvThirdTitle, binding.rvThird, 2)
    }

    private fun flipView(titleView: View, recyclerView: RecyclerView, initialPosition: Int) {
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
                if (viewModel.hotSignalBoards.value.isNotEmpty()) {
                    val currentPosition = (titleView.tag as? Int ?: initialPosition) + 3
                    val nextPosition = currentPosition % viewModel.hotSignalBoards.value.size
                    titleView.tag = nextPosition
                    (titleView as TextView).text =
                        viewModel.hotSignalBoards.value[nextPosition].title
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
        viewModel.clearBoard()
        findNavController().navigate(R.id.action_mainFragment_to_writePostFragment)
    }

    fun onMicClicked() {
        sttHelper.startSpeechToText()
    }

    fun searchKeyword() {
        val keyword = binding.etSearch.text.toString()
        viewModel.searchBoard(keyword)
        fadeOut(binding.ivSearch)
        fadeOut(binding.ivMic)
        fadeIn(binding.ivRefresh)
    }

    fun loadAllBoards() {
        viewModel.clearSearch()
        binding.etSearch.setText("")
        fadeIn(binding.ivSearch)
        fadeIn(binding.ivMic)
        fadeOut(binding.ivRefresh)
    }

    private fun fadeOut(view: View) {
        val fadeOut = AnimationUtils.loadAnimation(context, R.anim.anim_fade_out)
        view.startAnimation(fadeOut)
        view.visibility = View.GONE
    }

    private fun fadeIn(view: View) {
        view.visibility = View.VISIBLE
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_from_right_fade_in)
        view.startAnimation(fadeIn)
    }

    private fun setUpAdapter() {
        todayPostAdapter = TodayPostAdapter(
            onEndReached = {
//                viewModel.loadBoards()
            },
            onItemClicked = { board ->
                viewModel.selectBoard(board)
                findNavController().navigate(R.id.action_mainFragment_to_postFragment)
            },
            onTTSClicked = { content ->
                ttsHelper.speak(content)
            },
            viewModel = viewModel
        )

        firstTagAdapter = TagAdapter()
        binding.rvFirst.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = firstTagAdapter
        }

        secondTagAdapter = TagAdapter()
        binding.rvSecond.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = secondTagAdapter
        }

        thirdTagAdapter = TagAdapter()
        binding.rvThird.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = thirdTagAdapter
        }

        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayPostAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
//                        viewModel.loadBoards()
                    }
                }
            })
        }
    }

    private fun getSpannableString(
        fullText: String
    ): SpannableString {
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf("시그널")
        val end = start + "시그널".length
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#64FFCE")),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun setUpSpannableText() {
        val hotSpannable = getSpannableString("화제의 시그널")
        val todaySpannable = getSpannableString("오늘의 시그널")

        binding.tvHotSignal.text = hotSpannable
        binding.tvTodaySignal.text = todaySpannable
    }

    override fun onDestroyView() {
        ttsHelper.shutdown()
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}
