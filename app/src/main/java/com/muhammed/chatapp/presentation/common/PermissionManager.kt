package com.muhammed.chatapp.presentation.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

abstract class PermissionManager {

    companion object {

        private val CAMERA =
            Permission(permission = listOf(android.Manifest.permission.CAMERA), requestCode = 1)
        private val STORAGE = Permission(
            permission = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            requestCode = 2
        )

        fun requestPermission(activity: Activity, permission: Permissions) {
            val perm = getPermissionType(permission)
            ActivityCompat.requestPermissions(
                activity,
                perm.permission.toTypedArray(),
                perm.requestCode
            )
        }

        fun onPermissionResult(permission: Permissions, onSuccess: () -> Unit) {
            val perm = getPermissionType(permission)
            ActivityCompat.OnRequestPermissionsResultCallback { requestCode, _, grantResults ->
                if (requestCode == perm.requestCode) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        onSuccess()
                    }
                }
            }
        }

        fun hasCameraPermission(context: Context) = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        fun hasReadStoragePermission(context: Context) = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


        private fun getPermissionType(permission: Permissions): Permission {
            return when (permission) {
                Permissions.CAMERA -> CAMERA
                Permissions.STORAGE -> STORAGE
            }
        }

    }


    enum class Permissions {
        CAMERA, STORAGE
    }

    private data class Permission(
        val permission: List<String>,
        val requestCode: Int
    )

}