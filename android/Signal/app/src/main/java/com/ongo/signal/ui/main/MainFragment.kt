package com.ongo.signal.ui.main

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var todayPostAdapter: TodayPostAdapter

    override fun init() {
        setUpAdapter()

        lifecycleScope.launch {
            viewModel.posts.collectLatest { newPosts ->
                todayPostAdapter.submitList(newPosts)
            }
        }
    }

    private fun setUpAdapter() {
        todayPostAdapter = TodayPostAdapter {
            viewModel.loadPosts()
        }
        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(context)
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

