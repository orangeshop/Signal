package com.ongo.signal.ui.main.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.ItemPostBinding
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import timber.log.Timber

class ItemViewHolder(
    private val binding: ItemPostBinding,
    private val onItemClicked: (BoardDTO) -> Unit,
    private val onTTSClicked: (String) -> Unit,
    private val viewModel: BoardViewModel
) : RecyclerView.ViewHolder(binding.root) {

    private val chipAdapter = ChipAdapter()
    private val postImageAdapter = PostImageAdapter()

    init {
        binding.rvChips.apply {
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
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
        binding.userSession = UserSession

        binding.root.setOnClickListener {
            onItemClicked(board)
        }

        binding.ivTts.setOnClickListener {
            onTTSClicked(board.title)
        }

        binding.ivThumb.setOnClickListener {
            viewModel.onThumbClick(board, UserSession.userId ?: 0, ::bind)
        }

        binding.view.setOnClickListener {
            onItemClicked(board)
        }

        updateThumbIcon(board.isLiked)
        chipAdapter.submitList(board.tags)
        postImageAdapter.submitList(board.imageUrls)
        binding.executePendingBindings()
    }

    private fun updateThumbIcon(isLiked: Boolean) {
        binding.ivThumb.setImageResource(
            if (isLiked) R.drawable.baseline_thumb_up_alt_24_purple
            else R.drawable.baseline_thumb_up_off_alt_24
        )
    }
}
