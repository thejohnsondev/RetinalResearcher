package com.thejohnsondev.retinalresearcher.view.preview


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.common.util.concurrent.ListenableFuture
import com.thejohnsondev.retinalresearcher.R
import com.thejohnsondev.retinalresearcher.databinding.FragmentPreviewBinding
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BASE_PREVIEW_ROTATION
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.REQUEST_CODE_PERMISSION
import com.thejohnsondev.retinalresearcher.util.Util.getAppComponent
import com.thejohnsondev.retinalresearcher.util.YuvToRgbConverter
import com.thejohnsondev.retinalresearcher.util.base.BaseFragment
import jp.co.cyberagent.android.gpuimage.GPUImage
import java.util.concurrent.Executor
import javax.inject.Inject


class PreviewFragment : BaseFragment(R.layout.fragment_preview) {

    @Inject lateinit var rgbConverter: YuvToRgbConverter
    @Inject lateinit var processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    @Inject lateinit var cameraExecutor: Executor
    @Inject lateinit var imageAnalysis: ImageAnalysis
    private var cameraProvider: ProcessCameraProvider? = null
    private var currentBitmap: Bitmap? = null
    private val binding by viewBinding(FragmentPreviewBinding::bind)
    private val viewModel: PreviewViewModel by viewModels { getAppComponent().previewViewModelFactory() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun initListenersAndObservers() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSION)
        initPreview()
    }

    private fun initPreview() {
        binding.realtimePreview.apply {
            rotation = BASE_PREVIEW_ROTATION
            setScaleType(GPUImage.ScaleType.CENTER_CROP)
        }
        processCameraProviderFuture.addListener({
            cameraProvider = processCameraProviderFuture.get()
            startCameraIfReady()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCameraIfReady() {
        if (!viewModel.isPermissionGranted(requireContext())) return
        imageAnalysis.setAnalyzer(cameraExecutor, {
            var bitmap = allocateBitmapIfNecessary(it.width, it.height)
            rgbConverter.yuvToRgb(requireContext(), it.image!!, bitmap)
            it.close()
            binding.realtimePreview.post {
                binding.realtimePreview.setImage(bitmap)
            }
        })
        cameraProvider?.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis)
    }

    private fun allocateBitmapIfNecessary(width: Int, height: Int): Bitmap {
        if (currentBitmap == null || currentBitmap!!.width != width || currentBitmap!!.height != height) {
            currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        return currentBitmap!!
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            startCameraIfReady()
        }
    }

}