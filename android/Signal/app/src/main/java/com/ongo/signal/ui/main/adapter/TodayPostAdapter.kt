package com.ongo.signal.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.databinding.HeaderLayoutBinding
import com.ongo.signal.databinding.ItemPostBinding
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import timber.log.Timber

class TodayPostAdapter(
    private val onItemClicked: (BoardDTO) -> Unit,
    private val onTTSClicked: (String) -> Unit,
    private val viewModel: BoardViewModel,
    private val onTitleClicked: (Int) -> Unit
) : PagingDataAdapter<BoardDTO, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    init {
        addLoadStateListener { loadStates ->
            if (loadStates.refresh is LoadState.NotLoading || loadStates.refresh is LoadState.Error) {
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding =
                HeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding, viewModel, onTitleClicked)
        } else {
            val binding =
                ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding, onItemClicked, onTTSClicked, viewModel)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(viewModel.hotBoards.value)
        } else {
            val item = getItem(position - 1)
            item?.let {
                Timber.d("Binding item: $it at position: $position")
                (holder as ItemViewHolder).bind(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    fun updateItem(board: BoardDTO) {
        val position = snapshot().indexOfFirst { it?.id == board.id }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<BoardDTO>() {
        override fun areItemsTheSame(p0: BoardDTO, p1: BoardDTO): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: BoardDTO, p1: BoardDTO): Boolean {
            val isSame = p0 == p1
            Timber.d("Comparing contents: p0 = $p0, p1 = $p1, isSame = $isSame")
            return isSame
        }
    }
}
