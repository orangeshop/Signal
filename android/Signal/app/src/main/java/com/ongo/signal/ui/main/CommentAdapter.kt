package com.ongo.signal.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.databinding.ItemCommentBinding

class CommentAdapter : ListAdapter<CommentDTO, CommentAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentDTO) {
            binding.comment = comment
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CommentAdapter.ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<CommentDTO>() {
        override fun areItemsTheSame(p0: CommentDTO, p1: CommentDTO): Boolean {
            return p0.commentId == p1.commentId
        }

        override fun areContentsTheSame(p0: CommentDTO, p1: CommentDTO): Boolean {
            return p0 == p1
        }
    }
}