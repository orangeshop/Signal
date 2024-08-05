package com.ongo.signal.ui


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ongo.signal.R
import com.ongo.signal.config.BaseActivity
import com.ongo.signal.config.UserSession
import com.ongo.signal.databinding.ActivityMainBinding
import com.ongo.signal.ui.chat.fragment.ChatFragment
import com.ongo.signal.ui.main.fragment.MainFragment
import com.ongo.signal.ui.match.MatchFragment
import com.ongo.signal.ui.my.MyPageFragment
import com.ongo.signal.ui.video.CallActivity
import com.ongo.signal.ui.video.repository.VideoRepository
import com.ongo.signal.ui.video.service.VideoService
import com.ongo.signal.ui.video.service.VideoServiceRepository
import com.ongo.signal.ui.video.util.DataModel
import com.ongo.signal.ui.video.util.DataModelType
import com.ongo.signal.ui.video.util.getCameraAndMicPermission
import com.ongo.signal.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main){
    private val viewModel: MainViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment
    private val checker = PermissionChecker(this)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val runtimePermissions = arrayOf(
        Manifest.permission.CAMERA,
    )
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setupBinding(binding: ActivityMainBinding) {

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        handleIntent(intent, navController)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> showBottomNavigation()
                R.id.chatFragment -> showBottomNavigation()
                R.id.matchFragment -> showBottomNavigation()
                R.id.myPageFragment -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }

        checkPermission()

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        getCameraAndMicPermission {
            if (!checker.checkPermission(this, runtimePermissions)) {
                checker.setOnGrantedListener {
                    initFCM()
                }
                checker.requestPermissionLauncher.launch(runtimePermissions)
            } else {
                initFCM()
            }
        }
    }

    private fun initFCM() {
        getFCMToken()
        createNotificationChannel(CHANNEL_ID, "ongo")
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.d("FCM 토큰 얻기에 실패")
                return@OnCompleteListener
            }
            Timber.d("token 정보: ${task.result ?: "task.result is null"}")
            viewModel.postFCMToken(task.result)
        })
    }

    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(NotificationChannel(id, name, importance))
    }


    private fun handleBackPressed() {
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        val navController = navHostFragment.navController
        if (currentFragment is MainFragment || currentFragment is MatchFragment || currentFragment is ChatFragment || currentFragment is MyPageFragment) {
            finish()
        } else {
            navController.popBackStack()
        }
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun handleIntent(intent: Intent, navController: NavController) {
        val stringExtra = intent.getStringExtra("matchNotification")
        stringExtra?.let { stExtra ->
            val bundle = Bundle().apply {
                putString("matchNotification", stExtra)
                putLong("otherUserId", intent.getLongExtra("otherUserId", 0))
                putString("otherUserName", intent.getStringExtra("otherUserName") ?: "")
            }
            navController.navigate(R.id.matchFragment, bundle)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent, navHostFragment.navController)
    }

    companion object {
        const val CHANNEL_ID = "ongo_channel"
    }

}