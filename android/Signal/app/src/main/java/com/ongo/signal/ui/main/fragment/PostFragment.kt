package com.ongo.signal.ui.main.fragment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentPostBinding
import com.ongo.signal.ui.main.MainViewModel
import com.ongo.signal.ui.main.adapter.ChipAdapter
import com.ongo.signal.ui.main.adapter.CommentAdapter
import com.ongo.signal.util.PopupMenuHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var chipAdapter: ChipAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun init() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        commentAdapter = CommentAdapter()
        chipAdapter = ChipAdapter()

        binding.rvComment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        binding.rvChip.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = chipAdapter
        }

        viewModel.selectedBoard.value?.id?.let { boardId ->
            viewModel.loadBoardDetails(boardId)
            viewModel.loadComments(boardId)
        }


        lifecycleScope.launch {
            viewModel.selectedBoard.collectLatest { board ->
                board?.let {
                    Timber.d(viewModel.selectedBoard.value?.title)
                    Timber.d(viewModel.selectedBoard.value?.content)
//                    chipAdapter.submitList(it.tags)
                    commentAdapter.submitList(it.comments)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.comments.collectLatest { comments ->
                commentAdapter.submitList(comments)
                Timber.d(comments.toString())
            }
        }

        binding.fragment = this
    }

    fun showPopupMenu(view: View) {
        PopupMenuHelper.showPopupMenu(requireContext(), view, R.menu.popup_menu) { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(R.id.action_postFragment_to_writePostFragment)
                    true
                }

                R.id.action_delete -> {
                    viewModel.selectedBoard.value?.id?.let { viewModel.deleteBoard(it) }
                    findNavController().navigate(R.id.action_postFragment_to_mainFragment)
                    true
                }

                else -> false
            }
        }
    }

    fun createComment() {
        val content = binding.etComment.text.toString()
        val writer = "홍길동"
        val boardId = viewModel.selectedBoard.value?.id ?: return
        viewModel.createComment(boardId.toLong(), writer, content)
        binding.etComment.text.clear()
    }

    fun onProfileClick() {
        findNavController().navigate(R.id.action_postFragment_to_reviewFragment)
    }

}