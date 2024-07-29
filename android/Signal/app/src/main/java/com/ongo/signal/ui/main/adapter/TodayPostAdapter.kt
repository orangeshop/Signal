package com.ongo.signal.ui.main.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.databinding.ItemPostBinding

class TodayPostAdapter(
    private val onEndReached: () -> Unit,
    private val onItemClicked: (BoardDTO) -> Unit,
    private val onTTSClicked: (String) -> Unit
) : ListAdapter<BoardDTO, TodayPostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val chipAdapter = ChipAdapter()
        private val imageAdapter = ImageAdapter({ }, false)

        init {
            binding.rvChips.apply {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = chipAdapter
            }
            binding.rvImages.apply {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                adapter = imageAdapter
            }
        }

        fun bind(board: BoardDTO) {
            binding.board = board
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onItemClicked(board)
            }

            binding.ivTts.setOnClickListener {
                onTTSClicked(board.title)
            }

//            chipAdapter.submitList(board.tags)
//            imageAdapter.submitList(board.image?.map { Uri.parse(it.toString()) }) 추후 수정
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position == itemCount - 1) {
            onEndReached()
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
