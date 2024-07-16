package com.ongo.signal.ui

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ongo.signal.R
import com.ongo.signal.config.BaseActivity
import com.ongo.signal.databinding.ActivityMainBinding

class LoginActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_login){

    override fun setupBinding(binding: ActivityMainBinding) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
}