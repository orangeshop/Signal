package com.ongo.signal.ui.main.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMainBinding
import com.ongo.signal.ui.main.MainViewModel
import com.ongo.signal.ui.main.adapter.TagAdapter
import com.ongo.signal.ui.main.adapter.TodayPostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var todayPostAdapter: TodayPostAdapter
    private lateinit var firstTagAdapter: TagAdapter
    private lateinit var secondTagAdapter: TagAdapter
    private lateinit var thirdTagAdapter: TagAdapter

    override fun init() {
        binding.fragment = this
        binding.viewModel = viewModel
        setUpAdapter()

        lifecycleScope.launch {
            viewModel.posts.collectLatest { newPosts ->
                todayPostAdapter.submitList(newPosts)
                if (newPosts.isNotEmpty()) {
                    Timber.d(newPosts[0].tags.toString())
                    firstTagAdapter.submitList(newPosts[0].tags)
                    if (newPosts.size > 1) {
                        secondTagAdapter.submitList(newPosts[1].tags)
                    }
                    if (newPosts.size > 2) {
                        thirdTagAdapter.submitList(newPosts[2].tags)
                    }
                }
            }
        }
    }

    fun onFABClicked() {
        findNavController().navigate(R.id.action_mainFragment_to_writePostFragment)
    }

    private fun setUpAdapter() {
        todayPostAdapter = TodayPostAdapter(
            onEndReached = {
                viewModel.loadPosts()
            },
            onItemClicked = { post ->
                viewModel.selectPost(post)
                findNavController().navigate(R.id.action_mainFragment_to_postFragment)
            }
        )

        firstTagAdapter = TagAdapter()
        binding.rvFirst.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = firstTagAdapter
        }

        secondTagAdapter = TagAdapter()
        binding.rvSecond.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = secondTagAdapter
        }

        thirdTagAdapter = TagAdapter()
        binding.rvThird.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = thirdTagAdapter
        }

        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayPostAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        viewModel.loadPosts()
                    }
                }
            })
        }
    }
}

