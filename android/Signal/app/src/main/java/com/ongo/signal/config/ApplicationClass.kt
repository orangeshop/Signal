package com.ongo.signal.config

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
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

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        var keyHash = Utility.getKeyHash(this)
        Timber.tag("keyHash").d(keyHash)
    }
}