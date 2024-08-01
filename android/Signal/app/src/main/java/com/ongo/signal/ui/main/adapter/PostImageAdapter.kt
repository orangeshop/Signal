package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.databinding.ItemImagePostBinding

class PostImageAdapter : ListAdapter<String, PostImageAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemImagePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(url: String) {
                binding.url = url
                binding.executePendingBindings()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PostImageAdapter.ViewHolder {
        val binding = ItemImagePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostImageAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1
        }

        override fun areContentsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1
        }
    }
}