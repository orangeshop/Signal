package com.ongo.signal.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.chat.ChatAddListDTO
import com.ongo.signal.databinding.ChatAddTopItemBinding

class ChatAddTopAdapter() : ListAdapter<ChatAddListDTO, ChatAddTopAdapter.chatAddTopViewHolder>(diffUtil) {

    inner class chatAddTopViewHolder(val binding: ChatAddTopItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatAddListDTO){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chatAddTopViewHolder {
        return chatAddTopViewHolder(
            ChatAddTopItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: chatAddTopViewHolder, position: Int) {
        holder.bind(item = getItem(position))
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatAddListDTO>() {
            override fun areItemsTheSame(oldItem: ChatAddListDTO, newItem: ChatAddListDTO): Boolean {
                return oldItem.chat_id == newItem.chat_id
            }

            override fun areContentsTheSame(oldItem: ChatAddListDTO, newItem: ChatAddListDTO): Boolean {
                return oldItem == newItem
            }
        }
    }

}