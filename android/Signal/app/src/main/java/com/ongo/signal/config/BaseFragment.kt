package com.ongo.signal.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment<B : ViewDataBinding>(private val layoutResId: Int) : Fragment() {
    protected lateinit var binding: B
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setupBinding(binding)
        return binding.root
    }

    abstract fun setupBinding(binding: B)

    fun makeToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

}