package com.thejohnsondev.retinalresearcher.util

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.fragment.app.Fragment
import com.thejohnsondev.retinalresearcher.App
import com.thejohnsondev.retinalresearcher.di.component.AppComponent

object Util {

    fun Fragment.getAppComponent(): AppComponent =
        (requireActivity().application as App).appComponent

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}