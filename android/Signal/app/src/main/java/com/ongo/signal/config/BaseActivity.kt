package com.ongo.signal.config

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


abstract class BaseActivity<B : ViewDataBinding>(private val layoutResId: Int) :
    AppCompatActivity() {
    protected lateinit var binding: B
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
        setupBinding(binding)
    }

    abstract fun setupBinding(binding: B)

    fun makeToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}