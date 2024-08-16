package com.ongo.signal.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.ongo.signal.ui.main.ProgressDialog


abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes val layoutResId: Int
) : Fragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        init()
    }

    abstract fun init()

    protected fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        progressDialog?.show(childFragmentManager, ProgressDialog.TAG)
    }

    protected fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makeToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

}