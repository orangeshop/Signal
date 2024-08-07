package com.ongo.signal.ui.main.adapter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ongo.signal.R
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.HeaderLayoutBinding
import com.ongo.signal.databinding.ItemPostBinding
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.util.SpannableStringUtils

class TodayPostAdapter(
    private val onItemClicked: (BoardDTO) -> Unit,
    private val onTTSClicked: (String) -> Unit,
    private val viewModel: BoardViewModel,
    private val onTitleClicked: (Int) -> Unit
) : PagingDataAdapter<BoardDTO, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    inner class HeaderViewHolder(private val binding: HeaderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val firstTagAdapter = TagAdapter()
        private val secondTagAdapter = TagAdapter()
        private val thirdTagAdapter = TagAdapter()
        private val handler = Handler(Looper.getMainLooper())
        private val flipInterval = 3000L

        init {
            binding.viewModel = viewModel
//            binding.rvFirst.apply {
//                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
//                adapter = firstTagAdapter
//            }
//            binding.rvSecond.apply {
//                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
//                adapter = secondTagAdapter
//            }
//            binding.rvThird.apply {
//                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
//                adapter = thirdTagAdapter
//            }

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
//            binding.rvFirst.visibility = View.GONE
            binding.tvSecond.visibility = View.GONE
            binding.tvSecondTitle.visibility = View.GONE
//            binding.rvSecond.visibility = View.GONE
            binding.tvThird.visibility = View.GONE
            binding.tvThirdTitle.visibility = View.GONE
//            binding.rvThird.visibility = View.GONE
        }

        private fun showHotSignalViews(visibleCount: Int) {
            binding.tvFirst.visibility = if (visibleCount > 0) View.VISIBLE else View.GONE
            binding.tvFirstTitle.visibility = if (visibleCount > 0) View.VISIBLE else View.GONE
//            binding.rvFirst.visibility = if (visibleCount > 0) View.VISIBLE else View.GONE

            binding.tvSecond.visibility = if (visibleCount > 1) View.VISIBLE else View.GONE
            binding.tvSecondTitle.visibility = if (visibleCount > 1) View.VISIBLE else View.GONE
//            binding.rvSecond.visibility = if (visibleCount > 1) View.VISIBLE else View.GONE

            binding.tvThird.visibility = if (visibleCount > 2) View.VISIBLE else View.GONE
            binding.tvThirdTitle.visibility = if (visibleCount > 2) View.VISIBLE else View.GONE
//            binding.rvThird.visibility = if (visibleCount > 2) View.VISIBLE else View.GONE
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
//                    flipViews()
                    handler.postDelayed(this, flipInterval)
                }
            }, flipInterval)
        }

//        private fun flipViews() {
//            flipView(binding.tvFirstTitle, binding.rvFirst, 0)
//            flipView(binding.tvSecondTitle, binding.rvSecond, 1)
//            flipView(binding.tvThirdTitle, binding.rvThird, 2)
//        }

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

    inner class ItemViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val chipAdapter = ChipAdapter()
        private val postImageAdapter = PostImageAdapter()

        init {
            binding.rvChips.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = chipAdapter
            }
            binding.rvImages.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = postImageAdapter
            }
        }

        fun bind(board: BoardDTO) {
            binding.board = board
            binding.boardViewModel = viewModel
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onItemClicked(board)
            }

            binding.ivTts.setOnClickListener {
                onTTSClicked(board.title)
            }

            chipAdapter.submitList(board.tags)
            postImageAdapter.submitList(board.imageUrls)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(viewModel.hotBoards.value)
        } else {
            val item = getItem(position - 1)
            item?.let {
                (holder as ItemViewHolder).bind(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = HeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<BoardDTO>() {
        override fun areItemsTheSame(p0: BoardDTO, p1: BoardDTO): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: BoardDTO, p1: BoardDTO): Boolean {
            return p0 == p1
        }
    }
}
