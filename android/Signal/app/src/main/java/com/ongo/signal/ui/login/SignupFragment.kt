package com.ongo.signal.ui.login

import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMatchBinding
import com.ongo.signal.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(R.layout.fragment_signup) {
    override fun init() {
        initViews()
    }

    private fun initViews(){
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}