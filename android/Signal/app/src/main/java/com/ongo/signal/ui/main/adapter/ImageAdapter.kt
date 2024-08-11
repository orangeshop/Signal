package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.R
import com.ongo.signal.data.model.main.ImageItem
import com.ongo.signal.databinding.ItemImageUriBinding
import com.ongo.signal.databinding.ItemImageUrlBinding

class ImageAdapter(
    private val onRemoveClick: (ImageItem) -> Unit,
    private val showRemove: Boolean
) : ListAdapter<ImageItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ImageItem.UriItem -> R.layout.item_image_uri
            is ImageItem.UrlItem -> R.layout.item_image_url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_image_uri -> UriViewHolder(
                ItemImageUriBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            R.layout.item_image_url -> UrlViewHolder(
                ItemImageUrlBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UriViewHolder -> holder.bind(getItem(position) as ImageItem.UriItem)
            is UrlViewHolder -> holder.bind(getItem(position) as ImageItem.UrlItem)
        }
    }

    inner class UriViewHolder(private val binding: ItemImageUriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uriItem: ImageItem.UriItem) {
            binding.imageItem = uriItem
            binding.showRemove = showRemove
            binding.executePendingBindings()
            binding.ivRemove.setOnClickListener {
                onRemoveClick(uriItem)
            }
        }
    }

    inner class UrlViewHolder(private val binding: ItemImageUrlBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(urlItem: ImageItem.UrlItem) {
            binding.imageItem = urlItem
            binding.showRemove = showRemove
            binding.executePendingBindings()
            binding.ivRemove.setOnClickListener {
                onRemoveClick(urlItem)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return when {
                oldItem is ImageItem.UriItem && newItem is ImageItem.UriItem -> oldItem.uri == newItem.uri
                oldItem is ImageItem.UrlItem && newItem is ImageItem.UrlItem -> oldItem.url == newItem.url
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }

    fun addImage(imageItem: ImageItem) {
        val currentList = currentList.toMutableList()
        currentList.add(imageItem)
        submitList(currentList)
    }

    fun removeImage(imageItem: ImageItem) {
        val currentList = currentList.toMutableList()
        currentList.remove(imageItem)
        submitList(currentList)
    }
}
