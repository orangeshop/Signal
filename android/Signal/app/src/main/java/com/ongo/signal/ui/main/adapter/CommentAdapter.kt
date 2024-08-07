package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.databinding.ItemCommentBinding

class CommentAdapter(
    private val onCommentEditClick: (CommentDTOItem) -> Unit,
    private val onCommentDeleteClick: (CommentDTOItem) -> Unit,
    private val currentUserId: Long
) :
    ListAdapter<CommentDTOItem, CommentAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentDTOItem) {
            binding.comment = comment
            binding.currentUserId = currentUserId
            binding.tvEditComment.setOnClickListener { onCommentEditClick(comment) }
            binding.tvDeleteComment.setOnClickListener { onCommentDeleteClick(comment) }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<CommentDTOItem>() {
        override fun areItemsTheSame(p0: CommentDTOItem, p1: CommentDTOItem): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: CommentDTOItem, p1: CommentDTOItem): Boolean {
            return p0 == p1
        }
    }
}