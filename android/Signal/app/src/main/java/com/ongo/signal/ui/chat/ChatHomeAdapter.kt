package com.ongo.signal.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.ChatHomeDTO
import com.ongo.signal.databinding.ChatItemListBinding

private const val TAG = "ChatHomeAdapter"
class ChatHomeAdapter(
    private val chatListClickListener: () -> Unit,
    private val chatListLongClickListener: () -> Boolean
): ListAdapter<ChatHomeDTO, ChatHomeAdapter.ChatHomeListHolder>(diffUtil) {
    inner class ChatHomeListHolder(val binding: ChatItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeDTO){
            binding.chatItemTitle.text = item.list.get(0).name
            binding.content.text = item.list.get(0).content
            binding.Time.text = item.list.get(0).time
//            binding.Alarm.text = item.list.get(0).alarm.toString()
            binding.chatHomeCl.setOnClickListener {
                chatListClickListener()
            }

            binding.chatHomeCl.setOnLongClickListener {
                chatListLongClickListener()
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
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatHomeDTO, newItem: ChatHomeDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}