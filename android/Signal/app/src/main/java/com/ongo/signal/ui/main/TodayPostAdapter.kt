package com.ongo.signal.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.databinding.ItemPostBinding

class TodayPostAdapter(
    private val onEndReached: () -> Unit,
    private val onItemClicked: (PostDTO) -> Unit
) : ListAdapter<PostDTO, TodayPostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostDTO) {
            binding.post = post
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onItemClicked(post)
            }
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

    class DiffUtilCallback : DiffUtil.ItemCallback<PostDTO>() {
        override fun areItemsTheSame(p0: PostDTO, p1: PostDTO): Boolean {
            return p0.postId == p1.postId
        }

        override fun areContentsTheSame(p0: PostDTO, p1: PostDTO): Boolean {
            return p0 == p1
        }
    }
}
