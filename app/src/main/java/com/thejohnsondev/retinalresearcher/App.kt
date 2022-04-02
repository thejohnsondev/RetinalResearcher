package com.thejohnsondev.retinalresearcher

import android.app.Application
import com.thejohnsondev.retinalresearcher.di.component.AppComponent
import com.thejohnsondev.retinalresearcher.di.component.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }
}