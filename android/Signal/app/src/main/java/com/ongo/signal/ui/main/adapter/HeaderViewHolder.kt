package com.ongo.signal.ui.main.adapter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.R
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.HeaderLayoutBinding
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.util.SpannableStringUtils

class HeaderViewHolder(
    private val binding: HeaderLayoutBinding,
    private val viewModel: BoardViewModel,
    private val onTitleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val firstTagAdapter = TagAdapter()
    private val secondTagAdapter = TagAdapter()
    private val thirdTagAdapter = TagAdapter()
    private val handler = Handler(Looper.getMainLooper())
    private val flipInterval = 3000L

    init {
        binding.viewModel = viewModel

        binding.tvFirstTitle.setOnClickListener { onTitleClicked(0) }
        binding.tvSecondTitle.setOnClickListener { onTitleClicked(1) }
        binding.tvThirdTitle.setOnClickListener { onTitleClicked(2) }
        startFlippingViews()
    }

    fun bind(newHotBoards: List<BoardDTO>) {
        when (newHotBoards.size) {
            0 -> hideAllHotSignalViews()
            1 -> showHotSignalViews(1)
            2 -> showHotSignalViews(2)
            else -> showHotSignalViews(3)
        }

        updateHotSignalTitles(newHotBoards)
        updateTagAdapters(newHotBoards)
        setUpSpannableText()
        binding.executePendingBindings()
    }

    private fun hideAllHotSignalViews() {
        binding.tvFirst.visibility = View.GONE
        binding.tvFirstTitle.visibility = View.GONE
        binding.tvSecond.visibility = View.GONE
        binding.tvSecondTitle.visibility = View.GONE
        binding.tvThird.visibility = View.GONE
        binding.tvThirdTitle.visibility = View.GONE
    }

    private fun showHotSignalViews(visibleCount: Int) {
        binding.tvFirst.visibility = if (visibleCount > 0) View.VISIBLE else View.GONE
        binding.tvFirstTitle.visibility = if (visibleCount > 0) View.VISIBLE else View.GONE

        binding.tvSecond.visibility = if (visibleCount > 1) View.VISIBLE else View.GONE
        binding.tvSecondTitle.visibility = if (visibleCount > 1) View.VISIBLE else View.GONE

        binding.tvThird.visibility = if (visibleCount > 2) View.VISIBLE else View.GONE
        binding.tvThirdTitle.visibility = if (visibleCount > 2) View.VISIBLE else View.GONE
    }

    private fun updateHotSignalTitles(newHotBoards: List<BoardDTO>) {
        binding.tvFirstTitle.text = newHotBoards.getOrNull(0)?.title ?: ""
        binding.tvSecondTitle.text = newHotBoards.getOrNull(1)?.title ?: ""
        binding.tvThirdTitle.text = newHotBoards.getOrNull(2)?.title ?: ""
    }

    private fun updateTagAdapters(newBoards: List<BoardDTO>) {
        firstTagAdapter.submitList(newBoards.getOrNull(0)?.tags ?: emptyList())
        secondTagAdapter.submitList(newBoards.getOrNull(1)?.tags ?: emptyList())
        thirdTagAdapter.submitList(newBoards.getOrNull(2)?.tags ?: emptyList())
    }

    private fun setUpSpannableText() {
        val hotSpannable = SpannableStringUtils.getSpannableString(
            binding.root.context.getString(R.string.hotSignal),
            binding.root.context.getString(R.string.signal),
            "#64FFCE"
        )
        val todaySpannable = SpannableStringUtils.getSpannableString(
            binding.root.context.getString(R.string.realtime_signal),
            binding.root.context.getString(R.string.signal),
            "#64FFCE"
        )

        binding.tvHotSignal.text = hotSpannable
        binding.tvTodaySignal.text = todaySpannable
    }

    private fun startFlippingViews() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, flipInterval)
            }
        }, flipInterval)
    }

    private fun flipView(titleView: View, recyclerView: RecyclerView, position: Int) {
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
                if (viewModel.hotBoards.value.isNotEmpty()) {
                    val newPosition = position % viewModel.hotBoards.value.size
                    titleView.tag = newPosition
                    (titleView as TextView).text = viewModel.hotBoards.value[newPosition].title

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
}
