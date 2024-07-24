package com.ongo.signal.data.repository.main.chatnetworking

interface SaveDetailCallback {
    fun onSuccess()
    fun onFailure(exception: Exception)
}