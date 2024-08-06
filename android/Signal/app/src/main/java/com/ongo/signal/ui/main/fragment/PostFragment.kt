package com.ongo.signal.ui.main.fragment

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import com.ongo.signal.databinding.FragmentPostBinding
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.ui.main.viewmodel.CommentViewModel
import com.ongo.signal.ui.main.adapter.ChipAdapter
import com.ongo.signal.ui.main.adapter.CommentAdapter
import com.ongo.signal.ui.main.adapter.PostImageAdapter
import com.ongo.signal.util.KeyboardUtils
import com.ongo.signal.util.PopupMenuHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var chipAdapter: ChipAdapter
    private lateinit var imageAdapter: PostImageAdapter

    private val boardViewModel: BoardViewModel by activityViewModels()
    private val commentViewModel: CommentViewModel by activityViewModels()

    private var selectedCommentId: Long? = null

    override fun init() {
        binding.boardViewModel = boardViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupAdapters()
        observeViewModelData()
        loadInitialData()

        binding.fragment = this
    }

    private fun setupAdapters() {
        commentAdapter = CommentAdapter(
            onCommentEditClick = ::onCommentEditClick,
            onCommentDeleteClick = ::onCommentDeleteClick,
            currentUserId = boardViewModel.currentUserId
        )
        chipAdapter = ChipAdapter()
        imageAdapter = PostImageAdapter()

        binding.rvComment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        binding.rvChip.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = chipAdapter
        }

        binding.rvImages.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = imageAdapter
        }
    }

    private fun observeViewModelData() {
        lifecycleScope.launch {
            boardViewModel.selectedBoardDetail.collectLatest { board ->
                board?.let {
                    Timber.d("BoardDetailDTO received in fragment: $it")
                    imageAdapter.submitList(it.fileUrls)
                    chipAdapter.submitList(it.tags)
                }
            }
        }

        lifecycleScope.launch {
            commentViewModel.comments.collectLatest { comments ->
                commentAdapter.submitList(comments)
            }
        }
    }

    private fun loadInitialData() {
        boardViewModel.selectedBoard.value?.id?.let { boardId ->
            boardViewModel.loadBoardDetails(boardId)
            commentViewModel.loadComments(boardId)
        }
    }

    fun showPopupMenu(view: View) {
        PopupMenuHelper.showPopupMenu(requireContext(), view, R.menu.popup_menu) { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(R.id.action_postFragment_to_writePostFragment)
                    true
                }

                R.id.action_delete -> {
                    boardViewModel.selectedBoard.value?.id?.let { boardViewModel.deleteBoard(it) }
                    findNavController().popBackStack()
                    true
                }

                else -> false
            }
        }
    }

    private fun onCommentEditClick(comment: CommentDTOItem) {
        selectedCommentId = comment.id
        binding.etComment.setText(comment.content)
        KeyboardUtils.showKeyboard(binding.etComment)
    }

    private fun onCommentDeleteClick(comment: CommentDTOItem) {
        val boardId = boardViewModel.selectedBoard.value?.id ?: return
        commentViewModel.deleteComment(boardId, comment.id)
    }

    fun createComment() {
        val content = binding.etComment.text.toString()
        val writer = "홍길동"
        val boardId = boardViewModel.selectedBoard.value?.id ?: return
        val userId = boardViewModel.currentUserId

        if (selectedCommentId != null) {
            val commentRequest = CommentRequestDTO(boardId, userId.toLong(), content)
            commentViewModel.updateComment(boardId, selectedCommentId!!, commentRequest)
            selectedCommentId = null
        } else {
            commentViewModel.createComment(
                CommentDTOItem(
                    boardId = boardId,
                    userId = userId.toLong(),
                    writer = writer,
                    content = content
                )
            )
        }
        binding.etComment.text.clear()
        KeyboardUtils.hideKeyboard(binding.etComment)
    }

    fun onProfileClick() {
        findNavController().navigate(R.id.action_postFragment_to_reviewFragment)
    }
}