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

        lifecycleScope.launch {
            viewModel.posts.collectLatest { newPosts ->
                todayPostAdapter.submitList(newPosts)
                if (newPosts.isNotEmpty()) {
                    Timber.d(newPosts[0].tags.toString())
                    firstTagAdapter.submitList(newPosts[0].tags)
                    if (newPosts.size > 1) {
                        secondTagAdapter.submitList(newPosts[1].tags)
                    }
                    if (newPosts.size > 2) {
                        thirdTagAdapter.submitList(newPosts[2].tags)
                    }
                }
            }
        }

        startFlippingViews()
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
        flipView(binding.tvFirstTitle, binding.rvFirst)
        flipView(binding.tvSecondTitle, binding.rvSecond)
        flipView(binding.tvThirdTitle, binding.rvThird)
    }

    private fun flipView(titleView: View, recyclerView: RecyclerView) {
        val context = titleView.context
        val flipOut = AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet
        val flipIn = AnimatorInflater.loadAnimator(context, R.animator.flip_in) as AnimatorSet
        val flipOutRecycler = AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet
        val flipInRecycler = AnimatorInflater.loadAnimator(context, R.animator.flip_in) as AnimatorSet

        flipOut.setTarget(titleView)
        flipOutRecycler.setTarget(recyclerView)

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                val nextPosition = (titleView.tag as? Int ?: 0) + 1
                val nextPost = todayPostAdapter.currentList.getOrNull(nextPosition % todayPostAdapter.itemCount)
                if (nextPost != null) {
                    titleView.tag = nextPosition
                    (titleView as TextView).text = nextPost.title
                    (recyclerView.adapter as TagAdapter).submitList(nextPost.tags)
                }
                flipIn.setTarget(titleView)
                flipInRecycler.setTarget(recyclerView)
                flipIn.start()
                flipInRecycler.start()
            }
        })

        flipOut.start()
        flipOutRecycler.start()
    }

    fun onFABClicked() {
        viewModel.clearPost()
        findNavController().navigate(R.id.action_mainFragment_to_writePostFragment)
    }


    fun onMicClicked() {
        sttHelper.startSpeechToText()
    }

    private fun setUpAdapter() {
        todayPostAdapter = TodayPostAdapter(
            onEndReached = {
                viewModel.loadPosts()
            },
            onItemClicked = { post ->
                viewModel.selectPost(post)
                findNavController().navigate(R.id.action_mainFragment_to_postFragment)
            },
            onTTSClicked = { content ->
                ttsHelper.speak(content)
            }
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
                        viewModel.loadPosts()
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
