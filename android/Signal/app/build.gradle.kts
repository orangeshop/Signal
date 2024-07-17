plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.plugin)
    id("kotlin-kapt")
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
    //
    implementation(libs.paging)

}