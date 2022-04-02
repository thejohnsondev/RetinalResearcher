package com.thejohnsondev.retinalresearcher.domain.repo

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PreviewRepositoryImpl : PreviewRepository{
    override fun isPermissionGranted(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED

}