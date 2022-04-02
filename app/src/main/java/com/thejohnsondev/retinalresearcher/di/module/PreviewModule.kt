package com.thejohnsondev.retinalresearcher.di.module
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepository
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreviewModule  {

    @Singleton
    @Provides
    fun providePreviewRepository(): PreviewRepository = PreviewRepositoryImpl()

}