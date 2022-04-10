package com.thejohnsondev.retinalresearcher.view.preview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.SeekBar
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.common.util.concurrent.ListenableFuture
import com.thejohnsondev.retinalresearcher.R
import com.thejohnsondev.retinalresearcher.databinding.FragmentPreviewBinding
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_BRIGHTNESS
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_CONTRAST
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_GRAY_SCALE
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BASE_PREVIEW_ROTATION
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BRIGHTNESS_MAX
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BRIGHTNESS_MIN
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BRIGHTNESS_VALUE_DIVIDEND
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.CONTRAST_MAX
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.CONTRAST_MIN
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.CONTRAST_VALUE_DIVIDEND
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.REQUEST_CODE_PERMISSION
import com.thejohnsondev.retinalresearcher.util.Util.getAppComponent
import com.thejohnsondev.retinalresearcher.util.YuvToRgbConverter
import com.thejohnsondev.retinalresearcher.util.base.BaseFragment
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import java.util.concurrent.Executor
import javax.inject.Inject

class PreviewFragment : BaseFragment(R.layout.fragment_preview) {

    @Inject lateinit var rgbConverter: YuvToRgbConverter
    @Inject lateinit var processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    @Inject lateinit var cameraExecutor: Executor
    @Inject lateinit var imageAnalysis: ImageAnalysis
    private var cameraProvider: ProcessCameraProvider? = null
    private var currentBitmap: Bitmap? = null
    private var currentFilterOptionGroup: GPUImageFilterGroup? = null
    private val binding by viewBinding(FragmentPreviewBinding::bind)
    private val viewModel: PreviewViewModel by viewModels { getAppComponent().previewViewModelFactory() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun bindViews() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSION)
        initPreview()
        initActionFrame()
    }

    private fun initActionFrame() {
        initFilterOptions()
        initFilterValues()
        initFilterValuesListener()
    }

    private fun initFilterValues() {
        binding.actionsLayout.apply {
            contrastSeekBar.apply {
                max = CONTRAST_MAX
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    min = CONTRAST_MIN
                }
            }
            brightnessSeekBar.apply {
                max = BRIGHTNESS_MAX
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    min = BRIGHTNESS_MIN
                }
            }
        }
    }

    private fun initFilterValuesListener() {
        binding.actionsLayout.apply {
            contrastSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val contrastValue = p1.toFloat().div(CONTRAST_VALUE_DIVIDEND)
                    viewModel.setContrastValue(contrastValue)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}

            })
            brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val brightnessValue = p1.toFloat().div(BRIGHTNESS_VALUE_DIVIDEND)
                    viewModel.setBrightnessValue(brightnessValue)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}

            })
        }
    }

    private fun initFilterOptions() {
        binding.actionsLayout.apply {
            toggleContrast.setOnCheckedChangeListener { _, isSelected ->
                addRemoveOption(isSelected, OPTION_CONTRAST)
            }
            toggleBrightness.setOnCheckedChangeListener { _, isSelected ->
                addRemoveOption(isSelected, OPTION_BRIGHTNESS)
            }
            toggleGaryScale.setOnCheckedChangeListener { _, isSelected ->
                addRemoveOption(isSelected, OPTION_GRAY_SCALE)
            }
        }
    }

    private fun addRemoveOption(isSelected: Boolean, name: String) {
        viewModel.apply {
            if (isSelected) addFilterOption(name) else removeFilterOption(name)
        }
    }

    override fun initListenersAndObservers() {
        initFilterOptionListener()
    }

    private fun initFilterOptionListener() {
        viewModel.currentFilterOptions.observe(viewLifecycleOwner, {
            currentFilterOptionGroup = GPUImageFilterGroup(it.values.toList())
            binding.realtimePreview.filter = currentFilterOptionGroup
        })
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
        cameraProvider?.bindToLifecycle(
            viewLifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            imageAnalysis
        )
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