package com.ongo.signal.data.model.main

data class BoardImagesItemDTO(
    val boardId: Long,
    val chatRoomId: Long? = null,
    val fileName: String? = null,
    val fileType: Long? = null,
    val fileUrl: String,
    val id: Long? = null,
    val messageId: Long? = null,
    val userId: Long? = null
)
