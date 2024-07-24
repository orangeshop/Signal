package com.ongo.signal.data.model.match

data class MatchRegistrationRequest(
    val latitude: Double,
    val longitude: Double,
    val user_id: Long
)