package com.ongo.signal.ui.main.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.databinding.ItemPostBinding

class TodayPostAdapter(
    private val onEndReached: () -> Unit,
    private val onItemClicked: (PostDTO) -> Unit,
    private val onTTSClicked: (String) -> Unit
) : ListAdapter<PostDTO, TodayPostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val chipAdapter = ChipAdapter()
        private val imageAdapter = ImageAdapter({ }, false)

        init {
            binding.rvChips.apply {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = chipAdapter
            }
            binding.rvImages.apply {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                adapter = imageAdapter
            }
        }

        fun bind(post: PostDTO) {
            binding.post = post
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onItemClicked(post)
            }

            binding.ivTts.setOnClickListener {
                onTTSClicked(post.title)
            }

            chipAdapter.submitList(post.tags)
            imageAdapter.submitList(post.image?.map { Uri.parse(it.toString()) })
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
