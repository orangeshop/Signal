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
import com.ongo.signal.ui.main.adapter.TodayPostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var todayPostAdapter: TodayPostAdapter

    override fun init() {
        setUpAdapter()

        lifecycleScope.launch {
            viewModel.posts.collectLatest { newPosts ->
                todayPostAdapter.submitList(newPosts)
            }
        }

        binding.fabWrite.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_writePostFragment)
        }
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

