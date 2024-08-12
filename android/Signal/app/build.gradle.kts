import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.gms.google.services)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.ongo.signal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ongo.signal"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val naverClientId = properties["NAVER_CLIENT_ID"] ?: ""
        val naverClientSecret = properties["NAVER_CLIENT_SECRET"] ?: ""
        val kakaoNativeAppKey = properties["KAKAO_NATIVE_APP_KEY"] ?: ""
        val kakaoRestApiKey = properties["KAKAO_REST_API_KEY"] ?: ""

        buildConfigField("String", "NAVER_CLIENT_ID", "\"$naverClientId\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"$naverClientSecret\"")
        buildConfigField("String","KAKAO_NATIVE_APP_KEY","\"$kakaoNativeAppKey\"")
        buildConfigField("String","KAKAO_REST_API_KEY","\"$kakaoRestApiKey\"")
        manifestPlaceholders["kakaoScheme"] = "kakao$kakaoNativeAppKey"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    //retrofit
    implementation(libs.bundles.network)
    //viewmodel
    implementation(libs.viewmodel)
    implementation(libs.activity.ktx)
    //navigation
    implementation(libs.navigation.ktx)
    implementation(libs.navigation.ui)
    //implementation(libs.navigation.safe.args)
    //coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    //fragment
    implementation(libs.fragment.ktx)
    //glide
    implementation(libs.glide)
    //timber
    implementation(libs.timber)
    //lottie
    implementation(libs.lottie)

    //room
    implementation(libs.bundles.room)
    kapt(libs.room.compiler)
    //paging
    implementation(libs.paging)

    implementation(libs.bundles.squareup)
    implementation(libs.bundles.krossbow)

    //fcm
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.database)
    //locations
    implementation(libs.locations)
    //datastore
    implementation(libs.datastore)
    //webrtc
    implementation(libs.bundles.webrtc)

    //naver
    implementation(libs.naver)

    //kakao
    implementation(libs.kakao)
}