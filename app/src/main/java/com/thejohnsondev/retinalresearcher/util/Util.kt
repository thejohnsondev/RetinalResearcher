package com.thejohnsondev.retinalresearcher.util

import androidx.fragment.app.Fragment
import com.thejohnsondev.retinalresearcher.App
import com.thejohnsondev.retinalresearcher.di.component.AppComponent

object Util {

    fun Fragment.getAppComponent(): AppComponent =
        (requireActivity().application as App).appComponent

}