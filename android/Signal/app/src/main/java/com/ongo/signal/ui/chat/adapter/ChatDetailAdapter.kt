package com.ongo.signal.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.databinding.ChatDetailItemBinding

private const val TAG = "ChatDetailAdapter_싸피"

class ChatDetailAdapter(
    private val timeSetting: (item: String, target : Int) -> String
) : ListAdapter<ChatHomeChildDto, RecyclerView.ViewHolder>(diffUtil) {

    inner class ChatHomeOtherListHolder(val binding: ChatDetailItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeChildDto){
            binding.chatDetailItemMeTv.visibility = View.GONE
            binding.chatOtherReadMeTv.visibility = View.GONE
            binding.chatOtherTimeMeTv.visibility = View.GONE

            binding.today.text = timeSetting(item.sendAt, 2)

            binding.chatDetailItemTv.text = item.content
            binding.chatOtherReadTv.text = if(item.isRead == false) "1" else ""
            binding.chatOtherTimeTv.text = timeSetting(item.sendAt, 0)
        }
    }

    inner class ChatHomeMeListHolder(val binding: ChatDetailItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeChildDto){
            binding.chatDetailItemTv.visibility = View.GONE
            binding.chatOtherTimeTv.visibility = View.GONE
            binding.chatOtherReadTv.visibility = View.GONE

            binding.today.text = timeSetting(item.sendAt, 2)

            binding.chatDetailItemMeTv.text = item.content
            binding.chatOtherTimeMeTv.text = timeSetting(item.sendAt, 0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT -> ChatHomeOtherListHolder(
                ChatDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            RIGHT -> ChatHomeMeListHolder(
                ChatDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ChatHomeOtherListHolder -> holder.bind(item)
            is ChatHomeMeListHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(getItem(position).isFromSender){
            return 2
        }else{
            return 1
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatHomeChildDto>() {
            override fun areItemsTheSame(oldItem: ChatHomeChildDto, newItem: ChatHomeChildDto): Boolean {
                return oldItem.chatId == newItem.chatId
            }

            override fun areContentsTheSame(oldItem: ChatHomeChildDto, newItem: ChatHomeChildDto): Boolean {
                return oldItem == newItem
            }
        }

        const val LEFT = 1
        const val RIGHT = 2
    }
}