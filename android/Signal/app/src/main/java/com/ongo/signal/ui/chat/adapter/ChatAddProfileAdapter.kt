package com.ongo.signal.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.chat.ChatAddPeopleListDTO
import com.ongo.signal.databinding.ChatAddPeopleItemBinding

class ChatAddProfileAdapter() : ListAdapter<ChatAddPeopleListDTO, ChatAddProfileAdapter.ChatAddProfileViewHolder >(
    diffUtil) {

    inner class ChatAddProfileViewHolder(private val binding: ChatAddPeopleItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatAddPeopleListDTO) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAddProfileViewHolder {
        return ChatAddProfileViewHolder(
            ChatAddPeopleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatAddProfileViewHolder, position: Int) {
        holder.bind(item = getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatAddPeopleListDTO>() {
            override fun areItemsTheSame(
                oldItem: ChatAddPeopleListDTO,
                newItem: ChatAddPeopleListDTO
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: ChatAddPeopleListDTO,
                newItem: ChatAddPeopleListDTO
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}