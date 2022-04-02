package com.thejohnsondev.retinalresearcher

import android.app.Application
import com.thejohnsondev.retinalresearcher.di.component.AppComponent
import com.thejohnsondev.retinalresearcher.di.component.DaggerAppComponent
import com.thejohnsondev.retinalresearcher.di.module.PreviewModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .previewModule(PreviewModule(this))
            .build()
    }
}