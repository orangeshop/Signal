package com.ongo.signal.ui


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ongo.signal.R
import com.ongo.signal.config.BaseActivity
import com.ongo.signal.databinding.ActivityMainBinding
import com.ongo.signal.ui.chat.fragment.ChatFragment
import com.ongo.signal.ui.main.fragment.MainFragment
import com.ongo.signal.ui.match.MatchFragment
import com.ongo.signal.ui.my.MyPageFragment
import com.ssafy.firebase_b.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var navHostFragment: NavHostFragment
    private val checker = PermissionChecker(this)
    private val runtimePermissions = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.CAMERA,
    )

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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> showBottomNavigation()
                R.id.chatFragment -> showBottomNavigation()
                R.id.mapFragment -> showBottomNavigation()
                R.id.myPageFragment -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }

        checkPermission()
    }

    private fun checkPermission() {
        if (!checker.checkPermission(this, runtimePermissions)) {
            checker.setOnGrantedListener {
                initFCM()
            }
            checker.requestPermissionLauncher.launch(runtimePermissions)
        } else {
            initFCM()
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
            if (task.result != null) {
                uploadToken(task.result)
            }
        })
    }

    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(id, name, importance))
        }
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

    companion object {
        const val CHANNEL_ID = "ongo_channel"
        fun uploadToken(token: String) {

        }
    }


}