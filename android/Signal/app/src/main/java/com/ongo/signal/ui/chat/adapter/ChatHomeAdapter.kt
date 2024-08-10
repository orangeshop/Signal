package com.ongo.signal.ui.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ongo.signal.R
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.review.UserProfileResponse
import com.ongo.signal.databinding.ChatItemListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "ChatHomeAdapter_싸피"

class ChatHomeAdapter(
    private val chatItemClick: (item: ChatHomeDTO) -> Unit,
    private val chatItemLongClick: (item: ChatHomeDTO) -> Boolean,
    private val timeSetting: (item: String) -> String,
    private val userImageUrl: (item: ChatHomeDTO) -> String
) : ListAdapter<ChatHomeDTO, ChatHomeAdapter.ChatHomeListHolder>(diffUtil) {
    inner class ChatHomeListHolder(val binding: ChatItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatHomeDTO) {

            Glide.with(binding.root.context)
                .load(userImageUrl(item))
                .placeholder(R.drawable.basic_profile)
                .circleCrop()
                .into(binding.chatItemImg)



            binding.chatItemTitle.text =
                if (UserSession.userName == item.toName) item.fromName else item.toName;

            binding.content.text = item.lastMessage

            binding.Time.text = timeSetting(item.sendAt.toString())

            binding.chatHomeCl.setOnClickListener {
                chatItemClick(item)
            }

            binding.chatHomeCl.setOnLongClickListener {
                chatItemLongClick(item)
            }

            binding.Alarm.visibility = if(item.cnt > 0) View.VISIBLE else View.GONE
            binding.Alarm.text = item.cnt.toString()
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
                return oldItem.chatId == newItem.chatId
            }

            override fun areContentsTheSame(oldItem: ChatHomeDTO, newItem: ChatHomeDTO): Boolean {

                return oldItem == newItem
            }
        }
    }
}