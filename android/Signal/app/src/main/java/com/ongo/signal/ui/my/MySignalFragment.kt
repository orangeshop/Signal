package com.ongo.signal.ui.my

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMySignalBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.my.adapter.PreviewPostAdapter
import timber.log.Timber

class MySignalFragment : BaseFragment<FragmentMySignalBinding>(R.layout.fragment_my_signal) {

    private val previewPostAdapter = PreviewPostAdapter(onClick = {})
    private val viewModel: MyPageViewModel by activityViewModels()

    override fun init() {
        loadData()
        binding.rvPostPreview.adapter = previewPostAdapter
    }

    fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun loadData() {
        val userId = 3L
        viewModel.getMySignal(userId, onSuccess = { posts ->
            previewPostAdapter.submitList(posts)
        }, onError = { error ->
            Timber.e("Failed to load signals: ${error.message}")
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).showBottomNavigation()
    }
}