package com.thejohnsondev.retinalresearcher.di.component

import com.thejohnsondev.retinalresearcher.di.module.PreviewModule
import com.thejohnsondev.retinalresearcher.view.preview.PreviewFragment
import com.thejohnsondev.retinalresearcher.view.preview.PreviewViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [PreviewModule::class])
@Singleton
interface AppComponent {
    fun inject(previewFragment: PreviewFragment)
    fun previewViewModelFactory() : PreviewViewModelFactory
}