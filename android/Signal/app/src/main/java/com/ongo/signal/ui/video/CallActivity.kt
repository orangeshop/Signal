package com.ongo.signal.ui.video

import android.app.Activity
import android.content.Intent
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.ongo.signal.R
import com.ongo.signal.config.BaseActivity
import com.ongo.signal.databinding.ActivityCallBinding
import com.ongo.signal.ui.video.service.VideoService
import com.ongo.signal.ui.video.service.VideoServiceRepository
import com.ongo.signal.ui.video.util.convertToHumanTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CallActivity : BaseActivity<ActivityCallBinding>(R.layout.activity_call),
    VideoService.EndCallListener {

    private var target: String? = null
    private var isVideoCall: Boolean = true
    private var isCaller: Boolean = true
    private var targetName: String? = null

    private var isMicrophoneMuted = false
    private var isCameraMuted = false
    private var isScreenCasting = false

    @Inject
    lateinit var serviceRepository: VideoServiceRepository
    private lateinit var requestScreenCaptureLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        requestScreenCaptureLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                VideoService.screenPermissionIntent = intent
                isScreenCasting = true
                updateUiToScreenCaptureIsOn()
                serviceRepository.toggleScreenShare(true)
            }
        }
    }


    override fun setupBinding(binding: ActivityCallBinding) {
        init()
    }

    private fun init() {
        intent.getStringExtra("target")?.let {
            Timber.d("타겟 확인 ${it}")
            this.target = it
        } ?: kotlin.run {
            finish()
        }

        isVideoCall = intent.getBooleanExtra("isVideoCall", true)
        isCaller = intent.getBooleanExtra("isCaller", true)
        targetName = intent.getStringExtra("targetName")

        binding.apply {
            callTitleTv.text = "${targetName}"
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0..3600) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        callTimerTv.text = i.convertToHumanTime()
                    }
                }
            }

            if (!isVideoCall) {
                toggleCameraButton.isVisible = false
                switchCameraButton.isVisible = false

            }
            VideoService.remoteSurfaceView = remoteView
            VideoService.localSurfaceView = localView
            serviceRepository.setupViews(isVideoCall, isCaller, target!!)

            endCallButton.setOnClickListener {
                serviceRepository.sendEndCall()
            }

            switchCameraButton.setOnClickListener {
                serviceRepository.switchCamera()
            }
        }
        setupMicToggleClicked()
        setupCameraToggleClicked()
        VideoService.endCallListener = this

//        setSpeakerphoneOn(true)
    }

//    private fun setSpeakerphoneOn(on: Boolean) {
//        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val devices = audioManager.availableCommunicationDevices
//            val speakerDevice = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
//
//            if (on && speakerDevice != null) {
//                audioManager.setCommunicationDevice(speakerDevice)
//            } else {
//                audioManager.clearCommunicationDevice()
//            }
//        } else {
//            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
//            @Suppress("DEPRECATION")
//            audioManager.isSpeakerphoneOn = on
//        }
//    }


    private fun updateUiToScreenCaptureIsOn() {
        binding.apply {
            localView.isVisible = false
            switchCameraButton.isVisible = false
            toggleCameraButton.isVisible = false
        }
    }

    private fun setupMicToggleClicked() {
        binding.apply {
            toggleMicrophoneButton.setOnClickListener {
                if (!isMicrophoneMuted) {
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_off)
                    serviceRepository.toggleAudio(true)
                } else {
                    serviceRepository.toggleAudio(false)
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_on)
                }
                isMicrophoneMuted = !isMicrophoneMuted
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        serviceRepository.sendEndCall()
    }


    private fun setupCameraToggleClicked() {
        binding.apply {
            toggleCameraButton.setOnClickListener {
                if (!isCameraMuted) {
                    serviceRepository.toggleVideo(true)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_off)
                } else {
                    serviceRepository.toggleVideo(false)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_on)
                }

                isCameraMuted = !isCameraMuted
            }
        }
    }

    override fun onCallEnded() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
//        setSpeakerphoneOn(false)
        VideoService.remoteSurfaceView?.release()
        VideoService.remoteSurfaceView = null

        VideoService.localSurfaceView?.release()
        VideoService.localSurfaceView = null

        serviceRepository.stopService()
    }

}