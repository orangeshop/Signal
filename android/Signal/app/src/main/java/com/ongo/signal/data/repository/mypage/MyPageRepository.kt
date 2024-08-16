package com.ongo.signal.data.repository.mypage

import com.ongo.signal.data.model.login.LoginUserResponse
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.my.MyProfileResponse
import com.ongo.signal.data.model.my.ProfileEditRequest
import retrofit2.Response

interface MyPageRepository {

    suspend fun getMySignal(userId: Long): Response<List<BoardDTO>>

    suspend fun getMyCommentSignal(userId: Long): Response<List<BoardDTO>>

    suspend fun getMyProfile(): Result<MyProfileResponse?>

    suspend fun putUserProfile(
        userId: Long,
        request: ProfileEditRequest
    ): Result<LoginUserResponse?>
}