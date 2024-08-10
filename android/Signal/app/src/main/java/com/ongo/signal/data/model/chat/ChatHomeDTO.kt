package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "chat_home_table")
data class ChatHomeDTO(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("chat_id") var chatId: Long,

    @SerializedName("from_id") val fromId: Long,

    @SerializedName("to_id") val toId: Long,

    @SerializedName("from_name") val fromName: String,

    @SerializedName("to_name") val toName: String,

    @SerializedName("last_message") val lastMessage: String,

    @SerializedName("sender_type") val senderType: String,

    @SerializedName("send_at") var sendAt: Date, // 직렬화 시 JSON 필드명

    @SerializedName("from_url") var fromUrl : String?,

    @SerializedName("to_url") var toUrl : String?

)