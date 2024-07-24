package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.ReviewDTO
import com.ongo.signal.databinding.ItemReviewBinding

class ReviewAdapter : ListAdapter<ReviewDTO, ReviewAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewDTO) {
            binding.review = review
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ReviewAdapter.ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ReviewDTO>() {
        override fun areItemsTheSame(p0: ReviewDTO, p1: ReviewDTO): Boolean {
            return p0.reviewId == p1.reviewId
        }

        override fun areContentsTheSame(p0: ReviewDTO, p1: ReviewDTO): Boolean {
            return p0 == p1
        }
    }
}