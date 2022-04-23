package com.thejohnsondev.retinalresearcher.view.preview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.util.Range
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
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
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BASE_IMAGE_SIZE
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BASE_PREVIEW_ROTATION
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BRIGHTNESS_MAX
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BRIGHTNESS_MIN
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BRIGHTNESS_VALUE_DIVIDEND
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.CONTRAST_MAX
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.CONTRAST_MIN
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.CONTRAST_VALUE_DIVIDEND
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.REQUEST_CODE_PERMISSION
import com.thejohnsondev.retinalresearcher.util.Saved
import com.thejohnsondev.retinalresearcher.util.Saving
import com.thejohnsondev.retinalresearcher.util.Util.getAppComponent
import com.thejohnsondev.retinalresearcher.util.Util.getResolution
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
    private var camera: Camera? = null
    private var currentFilterOptionGroup: GPUImageFilterGroup? = null
    private val binding by viewBinding(FragmentPreviewBinding::bind)
    private val viewModel: PreviewViewModel by viewModels { getAppComponent().previewViewModelFactory() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
        initCaptureBtnListener()
        initOtherOptionsListener()
        initSaveStateObserver()
    }

    private fun initFilterOptionListener() {
        viewModel.currentFilterOptions.observe(viewLifecycleOwner, {
            currentFilterOptionGroup = GPUImageFilterGroup(it.values.toList())
            binding.realtimePreview.filter = currentFilterOptionGroup
        })
    }

    private fun initCaptureBtnListener() {
        binding.cameraShotBtn.setOnClickListener {
            saveCapture()
        }
    }

    private fun initOtherOptionsListener() {
        binding.apply {
            toggleTorch.setOnCheckedChangeListener { _, isSelected ->
                camera?.let {
                    if (it.cameraInfo.hasFlashUnit()) {
                        it.cameraControl.enableTorch(isSelected)
                    }
                }
            }
            toggleGrid.setOnCheckedChangeListener { _, isSelected ->
                val visibility = if (isSelected) VISIBLE else GONE
                gridLayout.visibility = visibility
            }
        }
    }

    private fun initSaveStateObserver() {
        viewModel.saveState.observe(viewLifecycleOwner, { state ->
            when(state) {
                is Saving -> {
                    binding.saveStateFrame.visibility = VISIBLE
                    binding.tvSaveState.text = getString(R.string.state_saving)
                }
                is Saved -> {
                    binding.saveStateFrame.visibility = GONE
                    binding.tvSaveState.text = getString(R.string.state_saved)
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("UnsafeOptInUsageError")
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

    private fun saveCapture() {
        viewModel.saveImage(binding.realtimePreview, requireContext(), getString(R.string.app_name))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCameraIfReady() {
        if (!viewModel.isPermissionGranted(requireContext())) return
        imageAnalysis.setAnalyzer(cameraExecutor, {
            val bitmap = allocateBitmapIfNecessary(it.width, it.height)
            rgbConverter.yuvToRgb(requireContext(), it.image!!, bitmap)
            it.close()
            binding.realtimePreview.post {
                binding.realtimePreview.setImage(bitmap)
            }
        })
        camera = cameraProvider?.bindToLifecycle(
            viewLifecycleOwner,
            DEFAULT_BACK_CAMERA,
            imageAnalysis
        )
        initCameraSpecs()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initCameraSpecs() {
        cameraProvider?.let { _ ->
            camera?.let { camera ->
                val camera2info = Camera2CameraInfo.from(camera.cameraInfo)

                val isoRange = camera2info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE) ?: Range(0,0)
                val resolution = camera2info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)?.getResolution()
                val focusDistance = camera2info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
                val aperture = camera2info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)?.firstOrNull()
                val oisEnabled = camera2info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)?.first() != 0

                binding.apply {
                    tvIsoValue.text = getString(R.string.iso_divider, isoRange.lower, isoRange.upper)
                    tvResolutionValue.text = resolution.toString()
                    tvLensFocus.text = focusDistance.toString()
                    tvApertureValue.text = getString(R.string.aperture_f, aperture.toString())
                    oisEnabledBtn.isChecked = oisEnabled
                    oisEnabledBtn.isClickable = false
                    imageSizeValue.text = getString(R.string.image_size_divider, BASE_IMAGE_SIZE, BASE_IMAGE_SIZE)
                }
            }
        }
    }

    private fun allocateBitmapIfNecessary(width: Int, height: Int): Bitmap {
        if (currentBitmap == null || currentBitmap!!.width != width || currentBitmap!!.height != height) {
            currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        return currentBitmap!!
    }

    @RequiresApi(Build.VERSION_CODES.N)
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