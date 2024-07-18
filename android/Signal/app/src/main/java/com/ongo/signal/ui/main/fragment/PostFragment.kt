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
import com.ongo.signal.ui.main.adapter.ChipAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var chipAdapter: ChipAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun init() {
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

        lifecycleScope.launch {
            viewModel.selectedPost.collectLatest { post ->
                post?.let {
                    binding.post = it
                    chipAdapter.submitList(it.tags)
                    commentAdapter.submitList(it.comment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearPost()
    }

}