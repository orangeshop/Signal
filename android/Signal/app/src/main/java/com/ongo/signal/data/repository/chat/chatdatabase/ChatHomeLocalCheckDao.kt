package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ongo.signal.data.model.chat.ChatHomeLocalCheckDTO

/**
 * 테이블의 구성은 chat id, 그날 처음 메시지를 보냈는지, 마지막으로 읽은 메시지의 인덱스, 보낸 시간 (yyyy-mm-dd)
 *
 * ex) 요일이 갱신 되었다면, 테이블에 채팅방 id, flase, 이전의 해당 채팅방의 마지막 테이블의 인덱스, 오늘 요일
 *
 * */
@Dao
interface ChatHomeLocalCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocalMessage(vararg chatHomeLocalCheckDTO: ChatHomeLocalCheckDTO)

    @Query("select * from chat_home_check_table where chatId = :chatId")
    suspend fun getLastMessageIndex(chatId: Long): ChatHomeLocalCheckDTO?

    @Query("update chat_home_check_table set lastReadMessageIndex = :lastReadMessageIndex where chatId = :chatId and sendAt = :sendAt")
    fun updateLastReadMessageIndex(chatId: Long, lastReadMessageIndex: Long, sendAt: String)

    @Query("update chat_home_check_table set  todayFirstSendMessageId = :todayFirstSendMessageId where chatId = :chatId and sendAt = :sendAt")
    fun updateTodayFirstMessage(chatId: Long, todayFirstSendMessageId: Long, sendAt: String)

    @Query("update chat_home_check_table set messageVolume = :messageVolume where ChatId = :ChatId and sendAt = :sendAt")
    fun updateMessageAmount(ChatId: Long, messageVolume: Long, sendAt: String)



}