package com.ongo.signal.data.model.chat

import android.util.Log
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val TAG = "DateConverters_싸피"
class DateConverter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val desiredFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("Asia/Seoul")
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date {
        val test = value?.let { Date(it) }
        Log.d(TAG, "fromTimestamp: ${test}")

        val test2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        test2.time = test

        return test2.time
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    fun fromIsoString(value: String?): Date? {
        return value?.let {
            try {
                dateFormat.parse(it)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun toIsoString(date: Date?): String? {
        return date?.let {
            dateFormat.format(it)
        }
    }
}