package com.ongo.signal.data.repository

import retrofit2.Response

interface SignalRepository {

    suspend fun getPost(id: Int): Response<Int>
}