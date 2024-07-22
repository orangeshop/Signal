package com.ongo.signal.util

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtil {
    private const val REQUEST_CODE = 1001

    fun checkAndRequestPermissions(fragment: Fragment, permissions: Array<String>): Boolean {
        val listPermissionsNeeded = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(permission)
            }
        }

        return if (listPermissionsNeeded.isNotEmpty()) {
            fragment.requestPermissions(listPermissionsNeeded.toTypedArray(), REQUEST_CODE)
            false
        } else {
            true
        }
    }

}
