package com.ongo.signal.ui.match.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.match.MatchPossibleUser
import com.ongo.signal.databinding.ItemPossibleUserBinding
import com.ongo.signal.util.tierSetting

class PossibleUserAdapter(
    private val onMatchClick: (userId: Long, userName: String) -> Unit,
    private val onClick: (userId: Long) -> Unit,
) :
    ListAdapter<MatchPossibleUser, PossibleUserAdapter.ViewHolder>(DiffUtilCallback()) {

    class ViewHolder(
        private val binding: ItemPossibleUserBinding,
        private val onMatchClick: (userId: Long, userName: String) -> Unit,
        private val onClick: (userId: Long) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: MatchPossibleUser) {
            binding.tvUserId.text = user.name
            binding.tvIntroduce.text = user.comment
            binding.btnMatching.setOnClickListener { onMatchClick(user.userId, user.name) }
            binding.ivGrade.setImageResource(tierSetting(user.score))
            binding.root.setOnClickListener { onClick(user.userId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val binding =
            ItemPossibleUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onMatchClick, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<MatchPossibleUser>() {
        override fun areItemsTheSame(p0: MatchPossibleUser, p1: MatchPossibleUser): Boolean {
            return p0.userId == p1.userId
        }

        override fun areContentsTheSame(p0: MatchPossibleUser, p1: MatchPossibleUser): Boolean {
            return p0 == p1
        }

    }
}