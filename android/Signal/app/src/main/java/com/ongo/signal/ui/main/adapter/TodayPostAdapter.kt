package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.ItemPostBinding
import com.ongo.signal.ui.main.BoardViewModel

class TodayPostAdapter(
    private val onItemClicked: (BoardDTO) -> Unit,
    private val onTTSClicked: (String) -> Unit,
    private val viewModel: BoardViewModel
) : PagingDataAdapter<BoardDTO, TodayPostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val chipAdapter = ChipAdapter()
        private val imageUriAdapter = ImageAdapter({ }, false)

        init {
            binding.rvChips.apply {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = chipAdapter
            }
            binding.rvImages.apply {
                layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = imageUriAdapter
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
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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
