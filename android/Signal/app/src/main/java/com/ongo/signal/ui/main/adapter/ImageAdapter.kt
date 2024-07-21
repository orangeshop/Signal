package com.ongo.signal.ui.main.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.databinding.ItemImageBinding
import timber.log.Timber

class ImageAdapter(private val onRemoveClick: (Uri) -> Unit) : ListAdapter<Uri, ImageAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            binding.uri = uri
            binding.executePendingBindings()
            binding.ivRemove.setOnClickListener {
                onRemoveClick(uri)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }

    fun addImage(uri: Uri) {
        val currentList = currentList.toMutableList()
        currentList.add(uri)
        submitList(currentList)
    }

    fun removeImage(uri: Uri) {
        val currentList = currentList.toMutableList()
        currentList.remove(uri)
        submitList(currentList)
    }
}
