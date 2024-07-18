package com.ongo.signal.ui.main.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.databinding.FragmentPostBinding
import com.ongo.signal.ui.main.adapter.CommentAdapter
import com.ongo.signal.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {

    private lateinit var commentAdapter: CommentAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun init() {
        commentAdapter = CommentAdapter()
        binding.rvComment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        lifecycleScope.launch {
            viewModel.selectedPost.collectLatest { post ->
                post?.let {
                    binding.post = it

                    val comments = listOf(
                        CommentDTO(commentId = "1", content = "This is a comment.", userId = "user1", userName = "User One"),
                        CommentDTO(commentId = "2", content = "This is another comment.", userId = "user2", userName = "User Two"),
                        CommentDTO(commentId = "3", content = "Yet another comment.", userId = "user3", userName = "User Three")
                    )

                    commentAdapter.submitList(comments)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearPost()
    }

}