package com.ongo.signal.config

import android.app.Application
import com.navercorp.nid.NaverIdLoginSDK
import com.ongo.signal.R
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.ongo.signal.BuildConfig

@HiltAndroidApp
class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        NaverIdLoginSDK.initialize(
            context = this,
            clientId = BuildConfig.NAVER_CLIENT_ID,
            clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
            clientName = getString(R.string.app_name)
        )

    }
}