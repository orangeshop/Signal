package com.ongo.signal.data.model.my

import okhttp3.MultipartBody

data class ProfileEditUiState(
    val imageFile: MultipartBody.Part? = null,
    val name: String,
    val type: String,
    val comment: String,
    val isProfileImageChanged: Boolean = false,
    val isProfileImageExisted: Boolean,
)