package com.ongo.signal.data.repository.login

import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse

interface LoginRepository {
    suspend fun postLogin(request: LoginRequest): Result<LoginResponse?>
}