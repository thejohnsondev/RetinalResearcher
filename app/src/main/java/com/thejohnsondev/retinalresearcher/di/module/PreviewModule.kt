package com.thejohnsondev.retinalresearcher.di.module
import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepository
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepositoryImpl
import com.thejohnsondev.retinalresearcher.util.YuvToRgbConverter
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class PreviewModule(
    private val context: Context
)  {

    @Singleton
    @Provides
    fun providePreviewRepository(): PreviewRepository = PreviewRepositoryImpl()

    @Provides
    fun provideProcessCameraProviderFuture() : ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)

    @Provides
    fun provideExecutor() : Executor = Executors.newSingleThreadExecutor()

    @Provides
    fun provideImageAnalysis() : ImageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

    @Provides
    fun provideYuvConverter() : YuvToRgbConverter = YuvToRgbConverter()

}