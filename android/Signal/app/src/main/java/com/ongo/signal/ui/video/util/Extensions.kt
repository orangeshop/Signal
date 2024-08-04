package com.ongo.signal.ui.video.util

import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.permissionx.guolindev.PermissionX

fun AppCompatActivity.getCameraAndMicPermission(success: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        PermissionX.init(this)
            .permissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            )
            .request { allGranted, _, _ ->

                if (allGranted) {
                    success()
                } else {
                    Toast.makeText(this, "camera and mic permission is required", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }else {
        PermissionX.init(this)
            .permissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
            )
            .request { allGranted, _, _ ->

                if (allGranted) {
                    success()
                } else {
                    Toast.makeText(this, "camera and mic permission is required", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}

fun Fragment.getCameraAndMicPermission(success: () -> Unit) {
    PermissionX.init(this)
        .permissions(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
        .request { allGranted, _, _ ->
            if (allGranted) {
                success()
            } else {
                Toast.makeText(requireContext(), "Camera and mic permission is required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
}

fun Int.convertToHumanTime(): String {
    val seconds = this % 60
    val minutes = this / 60
    val secondsString = if (seconds < 10) "0$seconds" else "$seconds"
    val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
    return "$minutesString:$secondsString"
}