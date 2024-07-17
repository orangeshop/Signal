package com.ongo.signal.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.databinding.ItemPostBinding

class TodayPostAdapter(
    private val onEndReached: () -> Unit
) : ListAdapter<PostDTO, TodayPostAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostDTO) {
            binding.post = post
            binding.executePendingBindings()
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

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostDTO>() {
            override fun areItemsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}
