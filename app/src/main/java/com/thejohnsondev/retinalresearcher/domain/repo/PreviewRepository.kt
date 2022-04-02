package com.thejohnsondev.retinalresearcher.domain.repo

import android.content.Context

interface PreviewRepository {
    fun isPermissionGranted(context: Context): Boolean
}