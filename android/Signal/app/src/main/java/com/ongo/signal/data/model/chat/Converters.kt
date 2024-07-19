package com.ongo.signal.data.model.chat

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun fromChatHomeChildDtoList(value: List<ChatHomeChildDto>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toChatHomeChildDtoList(value: String): List<ChatHomeChildDto> {
        val listType = object : TypeToken<List<ChatHomeChildDto>>() {}.type
        return Gson().fromJson(value, listType)
    }
}