package com.ongo.signal.ui.main.fragment

import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentWritePostBinding
import com.ongo.signal.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WritePostFragment : BaseFragment<FragmentWritePostBinding>(R.layout.fragment_write_post) {

    private lateinit var mainViewModel: MainViewModel

    override fun init() {
        binding.fragment = this
    }

    fun onRegisterButtonClick() {
        findNavController().navigate(R.id.action_writePostFragment_to_postFragment)
    }
}