package com.ongo.signal.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ongo.signal.R
import com.ongo.signal.config.DataStoreClass
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.login.SignalUser
import com.ongo.signal.ui.login.LoginViewModel
import com.ongo.signal.ui.video.firebase.FirebaseClient
import com.ongo.signal.ui.video.repository.VideoRepository
import dagger.hilt.android.AndroidEntryPoint
import fastcampus.part1.webrtctest.webrtc.WebRTCClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.internal.wait
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var videoRepository: VideoRepository

    @Inject
    lateinit var dataStoreClass: DataStoreClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({


//            Log.d("싸피", "onCreate: ${dataStoreClass.isLoginData.first()}")

            val intent = Intent(this@StartActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

            lifecycleScope.launch {
                if (dataStoreClass.isLoginData.first() == false) {
                    val intent = Intent(this@StartActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    viewModel.checkLogin { signalUser, userLoginId, userPassword ->
                        lifecycleScope.launch {
                            if (successLogin(signalUser, userLoginId, userPassword).isCompleted) {
                                val intent = Intent(this@StartActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }

        }, 400)

    }

    suspend private fun successLogin(
        signalUser: SignalUser?,
        userLoginId: String,
        userPassword: String,
    ): Deferred<Boolean> {
        val result = CompletableDeferred<Boolean>()

        lifecycleScope.launch {
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
                    refreshToken = signalUser.refreshToken,
                    userEncodePassword = signalUser.userEncodePassword
                )


                videoRepository.login(
                    UserSession.userId.toString(), userPassword
                ) { isDone, reason ->
                    if (!isDone) {
                        Log.d("싸피", "successLogin: 1")
                        result.complete(false)
                    } else {
                        Log.d("싸피", "successLogin: 2")
                        result.complete(true)
                    }
                }
            }
        }
        result.await()
        Log.d("싸피", "successLogin: ${result.isCompleted}")
        return result
    }
}