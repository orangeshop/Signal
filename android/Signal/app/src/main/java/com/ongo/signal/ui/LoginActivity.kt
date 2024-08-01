package com.ongo.signal.ui

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ongo.signal.R
import com.ongo.signal.config.BaseActivity
import com.ongo.signal.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    override fun setupBinding(binding: ActivityLoginBinding) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}