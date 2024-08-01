package com.ongo.signal.ui.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.ChatItemListBinding

private const val TAG = "ChatHomeAdapter_싸피"
class ChatHomeAdapter(
    private val chatItemClick: (item : ChatHomeDTO) -> Unit,
    private val chatItemLongClick: (item : ChatHomeDTO) -> Boolean,
    private val timeSetting: (item: String) -> String
): ListAdapter<ChatHomeDTO, ChatHomeAdapter.ChatHomeListHolder>(diffUtil) {
    inner class ChatHomeListHolder(val binding: ChatItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeDTO){

            binding.chatItemTitle.text = item.to_name

            binding.content.text = item.last_message

            binding.Time.text = timeSetting(item.send_at.toString())

            binding.chatHomeCl.setOnClickListener {
                chatItemClick(item)
            }

            binding.chatHomeCl.setOnLongClickListener {
                chatItemLongClick(item)
            }
        }
    }



    override fun onBindViewHolder(holder: ChatHomeListHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeListHolder {
        return ChatHomeListHolder(
            ChatItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatHomeDTO>() {
            override fun areItemsTheSame(oldItem: ChatHomeDTO, newItem: ChatHomeDTO): Boolean {
                return oldItem.chat_id == newItem.chat_id
            }

            override fun areContentsTheSame(oldItem: ChatHomeDTO, newItem: ChatHomeDTO): Boolean {

                return oldItem == newItem
            }
        }
    }
}