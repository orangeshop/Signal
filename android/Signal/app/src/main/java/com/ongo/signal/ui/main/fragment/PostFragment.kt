package com.ongo.signal.ui.main.fragment

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import com.ongo.signal.databinding.FragmentPostBinding
import com.ongo.signal.ui.main.adapter.ChipAdapter
import com.ongo.signal.ui.main.adapter.CommentAdapter
import com.ongo.signal.ui.main.adapter.PostImageAdapter
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.ui.main.viewmodel.CommentViewModel
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
        binding.apply {
            boardViewModel = this@PostFragment.boardViewModel
            userSession = UserSession
            fragment = this@PostFragment
        }

        setupAdapters()
        observeViewModelData()
        loadInitialData()
//        val drawable = if (boardViewModel.selectedBoard.value?.isLiked == true)
//            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_thumb_up_alt_24_purple)
//        else
//            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_thumb_up_off_alt_24)
//
//        binding.ivThumb.setImageDrawable(drawable)

        Timber.tag("selectedBoard")
            .d("selectedBoard type: ${boardViewModel.selectedBoard.value?.type}")
        Timber.tag("selectedBoard").d("userType: ${UserSession.userType}")
    }

    private fun setupAdapters() {
        commentAdapter = UserSession.userId?.let {
            CommentAdapter(
                onPopupMenuClick = ::showCommentPopupMenu,
                currentUserId = it
            )
        }!!
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
        viewLifecycleOwner.lifecycleScope.launch {
            boardViewModel.selectedBoard.collectLatest { board ->
                board?.let {
                    Timber.d("BoardDetailDTO received in fragment: $it")
                    binding.let { binding ->
                        imageAdapter.submitList(it.imageUrls)
                        chipAdapter.submitList(it.tags)
                        binding.tvComment.text = it.comments?.size?.toString() ?: "0"
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            commentViewModel.comments.collectLatest { comments ->
                binding.let { binding ->
                    Timber.tag("comments").d("submit")
                    commentAdapter.submitList(comments)
                    binding.tvComment.text = comments.size.toString()
                }
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
        binding.etComment.let { KeyboardUtils.showKeyboard(it) }
    }

    private fun onCommentDeleteClick(comment: CommentDTOItem) {
        val boardId = boardViewModel.selectedBoard.value?.id ?: return
        commentViewModel.deleteComment(boardId, comment.id)
    }

    fun createComment() {
        binding.let { binding ->
            val content = binding.etComment.text.toString()
            val writer = UserSession.userName
            val boardId = boardViewModel.selectedBoard.value?.id ?: return
            val userId = UserSession.userId

            if (content.isEmpty()) {
                makeToast("댓글을 입력해주세요.")
            } else {
                if (selectedCommentId != null) {
                    val commentRequest = userId?.let { CommentRequestDTO(boardId, it, content) }
                    if (commentRequest != null) {
                        commentViewModel.updateComment(boardId, selectedCommentId!!, commentRequest)
                    }
                    selectedCommentId = null
                } else {
                    if (userId != null) {
                        writer?.let {
                            CommentDTOItem(
                                boardId = boardId,
                                userId = userId.toLong(),
                                writer = it,
                                content = content
                            )
                        }?.let {
                            commentViewModel.createComment(
                                it
                            )
                        }
                    }
                }
                binding.etComment.text.clear()
            }

            KeyboardUtils.hideKeyboard(binding.etComment)
        }
    }

    private fun showCommentPopupMenu(view: View, comment: CommentDTOItem) {
        PopupMenuHelper.showPopupMenu(requireContext(), view, R.menu.popup_menu) { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    onCommentEditClick(comment)
                    true
                }

                R.id.action_delete -> {
                    onCommentDeleteClick(comment)
                    true
                }

                else -> false
            }
        }
    }

    fun onProfileClick() {
        findNavController().navigate(R.id.action_postFragment_to_reviewFragment)
    }
}