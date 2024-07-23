package com.ongo.signal.ui.my.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.databinding.ItemPostPreviewBinding
import com.ongo.signal.ui.main.adapter.ChipAdapter

class PreviewPostAdapter(
    private val onClick: () -> Unit,
) : ListAdapter<PostDTO, PreviewPostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemPostPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostDTO) {
            binding.post = post
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onClick()
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

    class DiffUtilCallback : DiffUtil.ItemCallback<PostDTO>() {
        override fun areItemsTheSame(p0: PostDTO, p1: PostDTO): Boolean {
            return p0.postId == p1.postId
        }

        override fun areContentsTheSame(p0: PostDTO, p1: PostDTO): Boolean {
            return p0 == p1
        }
    }
}
