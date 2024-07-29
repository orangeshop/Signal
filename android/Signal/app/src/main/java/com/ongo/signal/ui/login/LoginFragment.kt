package com.ongo.signal.ui.login

import android.content.Intent
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.databinding.FragmentLoginBinding
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.sign

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun init() {
        initViews()
    }

    private fun initViews() {
        binding.btnSignup.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fcv_login, SignupFragment())
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        binding.btnLogin.setOnClickListener {
            if (binding.tietId.text.toString().isBlank() || binding.tietPassword.text.toString().isBlank()) {
                makeToast("아이디나 비밀번호를 입력해주세요")
                return@setOnClickListener
            }
            viewModel.postLoginRequest(
                LoginRequest(
                    loginId = binding.tietId.text.toString(),
                    password = binding.tietPassword.text.toString()
                ),
                onSuccess = { signalUser ->
                    Timber.d("로그인 성공 $signalUser")
                    // TODO refactor 구조 ..
                    UserSession.userId = signalUser.userId
                    UserSession.userName = signalUser.userName
                    UserSession.accessToken = signalUser.accessToken

                    
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            )

        }
    }
}