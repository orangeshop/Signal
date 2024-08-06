package com.ongo.signal.ui.my.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.ItemPostPreviewBinding

class PreviewPostAdapter(
    private val onClick: (BoardDTO) -> Unit,
) : ListAdapter<BoardDTO, PreviewPostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemPostPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(boardDTO: BoardDTO) {
            binding.board = boardDTO
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onClick(boardDTO)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPostPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
