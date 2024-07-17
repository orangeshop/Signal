package com.ongo.signal.ui.login

import androidx.fragment.app.commit
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    override fun init() {
        Timber.d("확인")
        initViews()
    }

    private fun initViews() {
        binding.btnSignup.setOnClickListener {
            Timber.d("확인")
            parentFragmentManager.commit {
                replace(R.id.fcv_login, SignupFragment())
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }
}