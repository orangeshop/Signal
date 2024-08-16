package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.TagDTO
import com.ongo.signal.databinding.ItemChipBinding
import timber.log.Timber

class ChipAdapter : ListAdapter<TagDTO, ChipAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: TagDTO) {
            binding.tag = tag
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                Timber.d("Chip clicked: $tag")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ChipAdapter.ViewHolder {
        val binding = ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<TagDTO>() {
        override fun areItemsTheSame(p0: TagDTO, p1: TagDTO): Boolean {
            return p0.tagId == p1.tagId
        }

        override fun areContentsTheSame(p0: TagDTO, p1: TagDTO): Boolean {
            return p0 == p1
        }

    }
}