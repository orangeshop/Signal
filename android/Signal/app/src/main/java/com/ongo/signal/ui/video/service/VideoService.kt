package com.ongo.signal.ui.video.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ongo.signal.R
import com.ongo.signal.ui.video.repository.VideoRepository
import com.ongo.signal.ui.video.service.VideoServiceActions.END_CALL
import com.ongo.signal.ui.video.service.VideoServiceActions.SETUP_VIEWS
import com.ongo.signal.ui.video.service.VideoServiceActions.START_SERVICE
import com.ongo.signal.ui.video.service.VideoServiceActions.STOP_SERVICE
import com.ongo.signal.ui.video.service.VideoServiceActions.SWITCH_CAMERA
import com.ongo.signal.ui.video.service.VideoServiceActions.TOGGLE_AUDIO
import com.ongo.signal.ui.video.service.VideoServiceActions.TOGGLE_AUDIO_DEVICE
import com.ongo.signal.ui.video.service.VideoServiceActions.TOGGLE_SCREEN_SHARE
import com.ongo.signal.ui.video.service.VideoServiceActions.TOGGLE_VIDEO
import com.ongo.signal.ui.video.util.DataModel
import com.ongo.signal.ui.video.util.DataModelType
import com.ongo.signal.ui.video.util.isValid
import com.ongo.signal.ui.video.webrtc.RTCAudioManager
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@AndroidEntryPoint
class VideoService : Service(), VideoRepository.Listener {

    private val TAG = "MainService"

    private var isServiceRunning = false
    private var username: String? = null

    @Inject
    lateinit var videoRepository: VideoRepository

    private lateinit var notificationManager: NotificationManager
    private lateinit var rtcAudioManager: RTCAudioManager
    private var isPreviousCallStateVideo = true

    private lateinit var audioManager: AudioManager


    companion object {
        var listener: Listener? = null
        var endCallListener: EndCallListener? = null
        var localSurfaceView: SurfaceViewRenderer? = null
        var remoteSurfaceView: SurfaceViewRenderer? = null
        var screenPermissionIntent: Intent? = null
    }

    override fun onCreate() {
        super.onCreate()
        rtcAudioManager = RTCAudioManager.create(this)
        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
        notificationManager = getSystemService(
            NotificationManager::class.java
        )

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                START_SERVICE.name -> handleStartService(incomingIntent)
                SETUP_VIEWS.name -> handleSetupViews(incomingIntent)
                END_CALL.name -> handleEndCall()
                SWITCH_CAMERA.name -> handleSwitchCamera()
                TOGGLE_AUDIO.name -> handleToggleAudio(incomingIntent)
                TOGGLE_VIDEO.name -> handleToggleVideo(incomingIntent)
                TOGGLE_AUDIO_DEVICE.name -> handleToggleAudioDevice(incomingIntent)
                TOGGLE_SCREEN_SHARE.name -> handleToggleScreenShare(incomingIntent)
                STOP_SERVICE.name -> handleStopService()
                else -> Unit
            }
        }

        return START_STICKY
    }

    private fun handleStopService() {
        videoRepository.endCall()
        videoRepository.logOff {
            isServiceRunning = false
            stopSelf()
        }
    }

    private fun handleToggleScreenShare(incomingIntent: Intent) {
        val isStarting = incomingIntent.getBooleanExtra("isStarting", true)
        if (isStarting) {
            if (isPreviousCallStateVideo) {
                videoRepository.toggleVideo(true)
            }
            videoRepository.setScreenCaptureIntent(screenPermissionIntent!!)
            videoRepository.toggleScreenShare(true)

        } else {
            videoRepository.toggleScreenShare(false)
            if (isPreviousCallStateVideo) {
                videoRepository.toggleVideo(false)
            }
        }
    }

    private fun handleToggleAudioDevice(incomingIntent: Intent) {
        val type = when (incomingIntent.getStringExtra("type")) {
            RTCAudioManager.AudioDevice.EARPIECE.name -> RTCAudioManager.AudioDevice.EARPIECE
            RTCAudioManager.AudioDevice.SPEAKER_PHONE.name -> RTCAudioManager.AudioDevice.SPEAKER_PHONE
            else -> null
        }

        type?.let {
            rtcAudioManager.setDefaultAudioDevice(it)
            rtcAudioManager.selectAudioDevice(it)
            Log.d(TAG, "handleToggleAudioDevice: $it")
        }


    }

    private fun handleToggleVideo(incomingIntent: Intent) {
        val shouldBeMuted = incomingIntent.getBooleanExtra("shouldBeMuted", true)
        this.isPreviousCallStateVideo = !shouldBeMuted
        videoRepository.toggleVideo(shouldBeMuted)
    }

    private fun handleToggleAudio(incomingIntent: Intent) {
        val shouldBeMuted = incomingIntent.getBooleanExtra("shouldBeMuted", true)
        videoRepository.toggleAudio(shouldBeMuted)
    }

    private fun handleSwitchCamera() {
        videoRepository.switchCamera()
    }

    private fun handleEndCall() {
        videoRepository.sendEndCall()
        endCallAndRestartRepository()
    }

    private fun endCallAndRestartRepository() {
        videoRepository.endCall()
        endCallListener?.onCallEnded()
        videoRepository.initWebrtcClient(username!!)
    }

    private fun handleSetupViews(incomingIntent: Intent) {
        val isCaller = incomingIntent.getBooleanExtra("isCaller", false)
        val isVideoCall = incomingIntent.getBooleanExtra("isVideoCall", true)
        val target = incomingIntent.getStringExtra("target")
        this.isPreviousCallStateVideo = isVideoCall
        videoRepository.setTarget(target!!)

        videoRepository.initLocalSurfaceView(localSurfaceView!!, isVideoCall)
        videoRepository.initRemoteSurfaceView(remoteSurfaceView!!)


        if (!isCaller) {
            videoRepository.startCall()
        }

    }

    private fun handleStartService(incomingIntent: Intent) {
        if (!isServiceRunning) {
            isServiceRunning = true
            username = incomingIntent.getStringExtra("username")
            startServiceWithNotification()

            videoRepository.listener = this
            videoRepository.initFirebase()
            videoRepository.initWebrtcClient(username!!)

            increaseVolume()
        }
    }

    private fun increaseVolume() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)


        if (currentVolume < maxVolume) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                currentVolume + 6,
                AudioManager.FLAG_SHOW_UI
            )
        }
    }

    private fun startServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "channel1", "foreground", NotificationManager.IMPORTANCE_HIGH
            )

            val intent = Intent()

            val pendingIntent: PendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(
                this, "channel1"
            ).setSmallIcon(R.mipmap.ic_app_icon)
                .addAction(R.drawable.ic_end_call, "Exit", pendingIntent)

            startForeground(1, notification.build())
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLatestEventReceived(data: DataModel) {
        if (data.isValid()) {
            when (data.type) {
                DataModelType.StartVideoCall,
                DataModelType.StartAudioCall -> {
                    listener?.onCallReceived(data)
                }

                else -> Unit
            }
        }
    }

    override fun endCall() {
        //we are receiving end call signal from remote peer
        endCallAndRestartRepository()
    }

    interface Listener {
        fun onCallReceived(model: DataModel)
    }

    interface EndCallListener {
        fun onCallEnded()
    }
}