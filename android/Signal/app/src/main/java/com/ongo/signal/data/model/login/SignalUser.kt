package com.ongo.signal.data.model.login

data class SignalUser(
    val loginId: String,
    val userId: Long,
    val userName: String,
    val comment: String = "",
    val type: String,
    val accessToken: String,
    val accessTokenExpireTime: String,
)
