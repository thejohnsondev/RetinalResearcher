package com.thejohnsondev.retinalresearcher.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import androidx.fragment.app.Fragment
import com.thejohnsondev.retinalresearcher.App
import com.thejohnsondev.retinalresearcher.di.component.AppComponent
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.MEGAPIXEL
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.ZERO

object Util {

    fun Fragment.getAppComponent(): AppComponent =
        (requireActivity().application as App).appComponent

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, ZERO, ZERO, width, height, matrix, true)
    }

    fun Size.getResolution(): Int {
        return (this.height * this.width).div(MEGAPIXEL)
    }
}