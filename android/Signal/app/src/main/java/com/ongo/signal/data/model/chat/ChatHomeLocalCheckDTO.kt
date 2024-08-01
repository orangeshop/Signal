package com.ongo.signal.data.model.chat

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ChatHomeLocalCheckDTO (
    @PrimaryKey(autoGenerate = false)
    @SerializedName("chat_id") var chatId: Long,

    @SerializedName("today_first_send_message_id") val todayFirstSendMessageId: Long,

    @SerializedName("today_last_read_message_index") val todayLastSendMessageIndex: Long,



)