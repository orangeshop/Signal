package com.ongo.signal.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ongo.signal.R
import com.ongo.signal.config.UserSession
import com.ongo.signal.ui.MainActivity
import timber.log.Timber

interface FirebaseVideoReceivedListener {
    fun onCallReceivedFirebase(fromId: Long, fromName: String, status: String)
}

class SignalFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageTitle = remoteMessage.data.get("title").toString()
        val messageContent = remoteMessage.data.get("body").toString()
        Timber.d("메시지옴 제목 : ${messageTitle} 내용 : ${messageContent}")
        //승낙, 거부

        if (messageTitle.contains("매칭")) {
            val nowMessage = messageContent.split(" ")

            val mainIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("matchNotification", messageTitle)
                putExtra("otherUserId", nowMessage[0].toLong())
                putExtra("otherUserName", nowMessage[2])
            }

            val splitTitle = messageTitle.split(" ")
            var guideTitle = ""
            var guideContent = ""
            guideTitle = when (splitTitle[1]) {
                "요청" -> "매칭 신청"

                "승낙" -> "매칭 성공"

                "거부" -> "매칭 거부"
                else -> ""
            }
            guideContent = when (splitTitle[1]) {
                "요청" -> "${nowMessage[2]} 님께 매칭 신청이 왔습니다."

                "승낙" -> "${nowMessage[2]} 님이 매칭을 수락하였습니다!"

                "거부" -> "${nowMessage[2]} 님이 매칭을 거절하였습니다."
                else -> ""
            }

            val mainPendingIntent: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder1 = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(guideTitle)
                .setContentText(guideContent)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(101, builder1.build())

        } else if (messageTitle.contains("영통")) {
            val nowMessage = messageContent.split(" ")
            firebaseVideoReceivedListener?.onCallReceivedFirebase(
                fromId = nowMessage[0].toLong(),
                fromName = nowMessage[2],
                status = messageTitle.split(" ")[1]
            )
        } else {
            val nowTitle = messageTitle.split(" ")
            val otherId = nowTitle[1].toLong()

            if(UserSession.otherChatID != otherId) {
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }

                val mainPendingIntent: PendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val builder1 = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(nowTitle[0])
                    .setContentText(messageContent)
                    .setAutoCancel(true)
                    .setContentIntent(mainPendingIntent)

                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(101, builder1.build())
            }

        }
    }

    companion object {
        var firebaseVideoReceivedListener: FirebaseVideoReceivedListener? = null
    }

}

