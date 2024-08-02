package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chat_home_check_table")
data class ChatHomeLocalCheckDTO (
    @PrimaryKey(autoGenerate = false)
    @SerializedName("chat_id") val chatId: Long,

    @SerializedName("message_id") val messageId: Long,

    @SerializedName("today_first_send_message_id") val todayFirstSendMessageId: Long,

    @SerializedName("last_read_message_index") val lastReadMessageIndex: Long,

    @SerializedName("message_volume") val messageVolume: Long,

    @SerializedName("send_at") val sendAt: String
)