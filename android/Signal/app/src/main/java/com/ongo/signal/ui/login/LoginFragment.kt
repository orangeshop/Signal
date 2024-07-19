package com.ongo.signal.ui.login

import android.content.Intent
import androidx.fragment.app.commit
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentLoginBinding
import com.ongo.signal.ui.LoginActivity
import com.ongo.signal.ui.MainActivity
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

        binding.btnLogin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            requireActivity().finish()
        }
    }
}