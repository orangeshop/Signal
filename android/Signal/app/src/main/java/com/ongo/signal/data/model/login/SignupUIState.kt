package com.ongo.signal.data.model.login

data class SignupUIState(
    val userId: String,
    val password: String,
    val passwordCheck: String,
    val userName: String,
    val type: String = "주니어",
    val isPossibleId: Boolean? = null
)
