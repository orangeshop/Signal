package com.ongo.signal.data.model.main

data class BoardImagesItemDTO(
    val boardId: Int,
    val chatRoomId: Long?,
    val fileName: String?,
    val fileType: Long?,
    val fileUrl: String,
    val id: Int?,
    val messageId: Long?,
    val userId: Long?
)