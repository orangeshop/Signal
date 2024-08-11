package com.ongo.signal.ui.login

import android.content.Intent
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
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

    private fun setupNaverLoginButton() {
        NaverLoginCallback.setOnSuccessCallback { accessToken, _ ->
            viewModel.loginWithNaver(accessToken) { isSuccess, loginResponse ->
                if (isSuccess && loginResponse != null) {
                    val signalUser = SignalUser(
                        loginId = loginResponse.userInfo.loginId,
                        accessToken = loginResponse.accessToken,
                        accessTokenExpireTime = loginResponse.accessTokenExpireTime,
                        type = loginResponse.userInfo.type,
                        userId = loginResponse.userInfo.userId,
                        userName = loginResponse.userInfo.name,
                        refreshToken = loginResponse.refreshToken,
                        refreshTokenExpireTime = loginResponse.refreshTokenExpireTime
                    )
                    Timber.tag("naverLogin").d("success")
                    Timber.tag("userInfo").d("loginResponse: $loginResponse")
                    successLogin(
                        signalUser,
                        loginResponse.userInfo.loginId,
                        loginResponse.userInfo.password
                    )
                } else {
                    makeToast("네이버 로그인 실패")
                }
            }
        }

        NaverLoginCallback.setOnFailureCallback { errorMessage ->
            makeToast(errorMessage)
        }
    }

    fun onNaverLoginClicked() {
        NaverIdLoginSDK.authenticate(requireContext(), NaverLoginCallback)
    }

    fun kakaoLoginClicked() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Timber.e("카카오톡으로 로그인 실패: $error")
                } else if (token != null) {
                    Timber.d("카카오톡으로 로그인 성공, 토큰: ${token.accessToken}")
                    viewModel.handleKakaoLogin(token.accessToken) { isSuccess, loginResponse ->
                        if (isSuccess && loginResponse != null) {
                            val signalUser = SignalUser(
                                loginId = loginResponse.userInfo.loginId,
                                accessToken = loginResponse.accessToken,
                                accessTokenExpireTime = loginResponse.accessTokenExpireTime,
                                type = loginResponse.userInfo.type,
                                userId = loginResponse.userInfo.userId,
                                userName = loginResponse.userInfo.name,
                                refreshToken = loginResponse.refreshToken,
                                refreshTokenExpireTime = loginResponse.refreshTokenExpireTime
                            )
                            Timber.tag("kakaoLogin").d("success")
                            Timber.tag("userInfo").d("loginResponse: $loginResponse")
                            successLogin(
                                signalUser,
                                loginResponse.userInfo.loginId,
                                loginResponse.userInfo.password
                            )
                        } else {
                            makeToast("카카오 로그인 실패")
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext()) { token, error ->
                if (error != null) {
                    Timber.e("카카오계정으로 로그인 실패: $error")
                } else if (token != null) {
                    Timber.d("카카오 계정으로 로그인 성공, 토큰: ${token.accessToken}")
                    viewModel.handleKakaoLogin(token.accessToken) { isSuccess, loginResponse ->
                        if (isSuccess && loginResponse != null) {
                            val signalUser = SignalUser(
                                loginId = loginResponse.userInfo.loginId,
                                accessToken = loginResponse.accessToken,
                                accessTokenExpireTime = loginResponse.accessTokenExpireTime,
                                type = loginResponse.userInfo.type,
                                userId = loginResponse.userInfo.userId,
                                userName = loginResponse.userInfo.name,
                                refreshToken = loginResponse.refreshToken,
                                refreshTokenExpireTime = loginResponse.refreshTokenExpireTime
                            )
                            Timber.tag("kakao").d("success")
                            Timber.tag("userInfo").d("loginResponse: $loginResponse")
                            successLogin(
                                signalUser,
                                loginResponse.userInfo.loginId,
                                loginResponse.userInfo.password
                            )
                        } else {
                            makeToast("카카오 로그인 실패")
                        }
                    }
                }
            }
        }
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