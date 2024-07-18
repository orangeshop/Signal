package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.TagDTO
import com.ongo.signal.databinding.ItemTagBinding

class TagAdapter : ListAdapter<TagDTO, TagAdapter.ViewHolder>(DiffUtilCallback()) {
    inner class ViewHolder(private val binding: ItemTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: TagDTO) {
            binding.tag = tag
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<TagDTO>() {
        override fun areItemsTheSame(oldItem: TagDTO, newItem: TagDTO): Boolean {
            return oldItem.tagId == newItem.tagId
        }

        override fun areContentsTheSame(oldItem: TagDTO, newItem: TagDTO): Boolean {
            return oldItem == newItem
        }

    }
}