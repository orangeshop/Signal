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
import com.ongo.signal.data.model.main.ImageItem
import com.ongo.signal.databinding.FragmentPostBinding
import com.ongo.signal.ui.main.MainViewModel
import com.ongo.signal.ui.main.adapter.ChipAdapter
import com.ongo.signal.ui.main.adapter.CommentAdapter
import com.ongo.signal.ui.main.adapter.ImageAdapter
import com.ongo.signal.ui.main.adapter.PostImageAdapter
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
    private val viewModel: MainViewModel by activityViewModels()
    private var selectedCommentId: Long? = null

    override fun init() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        commentAdapter = CommentAdapter(
            onCommentEditClick = { comment ->
                onCommentEditClick(comment)
            },
            onCommentDeleteClick = { comment ->
                onCommentDeleteClick(comment.id)
            },
            currentUserId = viewModel.currentUserId.value
        )
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

        setupImageAdapter()

        viewModel.selectedBoard.value?.id?.let { boardId ->
            viewModel.loadBoardDetails(boardId)
            viewModel.loadComments(boardId)
        }

        Timber.d(viewModel.selectedBoard.value?.imageUrls.toString())
        Timber.d(viewModel.selectedBoard.value?.id.toString())
        Timber.d(viewModel.selectedBoard.value?.title ?: "title is Null")
        Timber.d(viewModel.selectedBoard.value?.content ?: "content is Null")
        Timber.d(viewModel.selectedBoard.value.toString())

        lifecycleScope.launch {
            viewModel.selectedBoard.collectLatest { board ->
                board?.let {
                    val imageUrls = it.imageUrls
                    val comments = it.comments
                    val tags = it.tags

                    imageAdapter.submitList(imageUrls)
                    commentAdapter.submitList(comments)
                    chipAdapter.submitList(tags)
                    Timber.d("recycle")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.comments.collectLatest { comments ->
                commentAdapter.submitList(comments)
            }
        }

        binding.fragment = this
    }

    private fun setupImageAdapter() {
        imageAdapter = PostImageAdapter()

        binding.rvImages.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = imageAdapter
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
                    viewModel.selectedBoard.value?.id?.let { viewModel.deleteBoard(it) }
                    findNavController().navigate(R.id.action_postFragment_to_mainFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun onCommentEditClick(comment: CommentDTOItem) {
        selectedCommentId = comment.id.toLong()
        binding.etComment.setText(comment.content)
        showKeyboard()
    }

    private fun onCommentDeleteClick(commentId: Int) {
        val boardId = viewModel.selectedBoard.value?.id ?: return
        viewModel.deleteComment(boardId, commentId.toLong())
    }

    fun createComment() {
        val content = binding.etComment.text.toString()
        val writer = "홍길동"
        val boardId = viewModel.selectedBoard.value?.id ?: return
        if (selectedCommentId != null) {
            viewModel.updateComment(boardId.toLong(), selectedCommentId!!, content)
            selectedCommentId = null
        } else {
            viewModel.createComment(boardId.toLong(), writer, content)
        }
        binding.etComment.text.clear()
        hideKeyboard()
    }

    private fun showKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.etComment.requestFocus()
        imm.showSoftInput(binding.etComment, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etComment.windowToken, 0)
    }

    fun onProfileClick() {
        findNavController().navigate(R.id.action_postFragment_to_reviewFragment)
    }

    fun onThumbClick(board: BoardDTO?) {
        if (board != null) {
            viewModel.onThumbClick(board)
        }
    }
}