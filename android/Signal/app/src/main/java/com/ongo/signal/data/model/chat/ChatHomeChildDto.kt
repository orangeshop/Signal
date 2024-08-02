package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chat_detail_table")
data class ChatHomeChildDTO(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("message_id") var messageId: Long,

    @SerializedName("chat_id") val chatId: Long,

    @SerializedName("is_from_sender") val isFromSender: Boolean,

    @SerializedName("content") val content: String,

    @SerializedName("is_read") val isRead: Boolean,

    @SerializedName("send_at") var sendAt: String // 날짜를 문자열로 직렬화
)