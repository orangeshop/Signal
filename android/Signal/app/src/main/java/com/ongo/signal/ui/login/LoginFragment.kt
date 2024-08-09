package com.ongo.signal.ui.login

import android.content.Intent
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.SignalUser
import com.ongo.signal.databinding.FragmentLoginBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.video.repository.VideoRepository
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var videoRepository: VideoRepository

    override fun init() {
        checkLogin()
        initViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this
        binding.executePendingBindings()
        setupNaverLoginButton()
    }

    private fun checkLogin() {
        viewModel.checkLogin { signalUser, userLoginId, userPassword ->
            successLogin(signalUser, userLoginId, userPassword)
        }
    }

    fun setupNaverLoginButton() {
        NaverLoginCallback.setOnSuccessCallback { accessToken, refreshToken ->
            viewModel.loginWithNaver(accessToken)
        }

        NaverLoginCallback.setOnFailureCallback { errorMessage ->
            makeToast(errorMessage)
        }

        binding.nolbLogin.setOAuthLogin(NaverLoginCallback)
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
            if (binding.tietId.text.toString().isBlank() || binding.tietPassword.text.toString()
                    .isBlank()
            ) {
                makeToast("아이디나 비밀번호를 입력해주세요")
                return@setOnClickListener
            }
            viewModel.postLoginRequest(
                LoginRequest(
                    loginId = binding.tietId.text.toString(),
                    password = binding.tietPassword.text.toString()
                ),
                onSuccess = { isSuccess, signalUser ->
                    if (isSuccess) {
                        successLogin(
                            signalUser,
                            binding.tietId.text.toString(),
                            binding.tietPassword.text.toString()
                        )
                    } else {
                        makeToast("아이디나 비밀번호를 확인해주세요")
                    }
                }
            )

        }
    }

    private fun successLogin(
        signalUser: SignalUser?,
        userLoginId: String,
        userPassword: String,
    ) {
        signalUser?.let {
            UserSession.userId = signalUser.userId
            UserSession.userName = signalUser.userName
            UserSession.accessToken = signalUser.accessToken
            UserSession.refreshToken = signalUser.refreshToken
            UserSession.userType = signalUser.type

            Timber.d("로그인 완료 유저 정보 ${UserSession.userId} ${UserSession.userName} ${UserSession.accessToken}")

            viewModel.saveUserData(
                userId = signalUser.userId,
                userLoginId = userLoginId,
                userName = signalUser.userName,
                userPassword = userPassword,
                profileImage = "",
                accessToken = signalUser.accessToken,
                refreshToken = signalUser.refreshToken
            )

            videoRepository.login(
                UserSession.userId.toString(), userPassword
            ) { isDone, reason ->
                if (!isDone) {
                    makeToast(reason.toString())
                } else {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }
}