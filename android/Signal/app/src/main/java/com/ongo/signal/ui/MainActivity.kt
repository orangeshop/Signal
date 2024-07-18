package com.ongo.signal.ui

import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseActivity
import com.ongo.signal.databinding.ActivityMainBinding
import com.ongo.signal.ui.chat.ChatFragment
import com.ongo.signal.ui.main.fragment.MainFragment
import com.ongo.signal.ui.match.MatchFragment
import com.ongo.signal.ui.my.MyPageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var navHostFragment: NavHostFragment

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

}