package com.ongo.signal.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.databinding.ChatDetailItemBinding
import com.ongo.signal.databinding.ChatItemListBinding

class ChatDetailAdapter() : ListAdapter<ChatHomeChildDto, ChatDetailAdapter.ChatHomeChildListHolder>(diffUtil) {

    inner class ChatHomeChildListHolder(val binding: ChatDetailItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeChildDto){
            binding.chatDetailItemTv.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeChildListHolder {
        return ChatHomeChildListHolder(
            ChatDetailItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatHomeChildListHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatHomeChildDto>() {
            override fun areItemsTheSame(oldItem: ChatHomeChildDto, newItem: ChatHomeChildDto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatHomeChildDto, newItem: ChatHomeChildDto): Boolean {
                return oldItem == newItem
            }
        }
    }
}