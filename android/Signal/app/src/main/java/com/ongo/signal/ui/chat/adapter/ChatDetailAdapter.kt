package com.ongo.signal.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ongo.signal.R
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeChildDTO
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.databinding.ChatDetailItemBinding

private const val TAG = "ChatDetailAdapter_싸피"

class ChatDetailAdapter(
    private val timeSetting: (item: String, target : Int) -> String,
    private val todaySetting: (id: Long,item: Long, time: String) -> Boolean,
    private val fromID : Long,
    private val userImageUrl: () -> String,
    private val chatItemClick: (item: ChatHomeDTO) -> Unit,
) : ListAdapter<ChatHomeChildDTO, RecyclerView.ViewHolder>(diffUtil) {

    inner class ChatHomeOtherListHolder(val binding: ChatDetailItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeChildDTO){

            binding.chatDetailItemMeTv.visibility = View.GONE
            binding.chatOtherReadMeTv.visibility = View.GONE
            binding.chatOtherTimeMeTv.visibility = View.GONE

            binding.today.visibility = View.GONE

            Glide.with(binding.root.context)
                .load(userImageUrl())
                .placeholder(R.drawable.basic_profile)
                .circleCrop()
                .into(binding.chatDetailItemImg)

            binding.today.visibility = if(todaySetting(item.messageId,item.chatId, timeSetting(item.sendAt, 2)) == true) View.VISIBLE else View.GONE
            binding.today.text = timeSetting(item.sendAt, 2)


            binding.chatDetailItemTv.text = item.content
            binding.chatOtherReadTv.text = if(item.isRead == false) "1" else ""
            binding.chatOtherTimeTv.text = timeSetting(item.sendAt, 1)

        }
    }

    inner class ChatHomeMeListHolder(val binding: ChatDetailItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatHomeChildDTO){
            binding.chatDetailItemTv.visibility = View.GONE
            binding.chatOtherTimeTv.visibility = View.GONE
            binding.chatOtherReadTv.visibility = View.GONE
            binding.chatDetailItemImg.visibility = View.GONE

            binding.today.visibility = View.GONE

            binding.today.visibility = if(todaySetting(item.messageId,item.chatId, timeSetting(item.sendAt, 2)) == true) View.VISIBLE else View.GONE
            binding.today.text = timeSetting(item.sendAt, 2)

            binding.chatDetailItemMeTv.text = item.content
            binding.chatOtherTimeMeTv.text = timeSetting(item.sendAt, 1)

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
        val isFrom = UserSession.userId == fromID
        return if(isFrom == getItem(position).isFromSender) RIGHT else LEFT
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatHomeChildDTO>() {
            override fun areItemsTheSame(oldItem: ChatHomeChildDTO, newItem: ChatHomeChildDTO): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: ChatHomeChildDTO, newItem: ChatHomeChildDTO): Boolean {
                return oldItem == newItem
            }
        }

        const val LEFT = 1
        const val RIGHT = 2
    }
}