package com.thejohnsondev.retinalresearcher.view.preview

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepository
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_BRIGHTNESS
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_CONTRAST
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_EMPTY
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_GRAY_SCALE
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_RGB
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_SATURATION
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_WHITE_BALANCE
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BASE_IMAGE_SIZE
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BASE_PREVIEW_ROTATION
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.DEFAULT_CONTRAST_VALUE
import com.thejohnsondev.retinalresearcher.util.MediaManager
import com.thejohnsondev.retinalresearcher.util.SaveState
import com.thejohnsondev.retinalresearcher.util.Saved
import com.thejohnsondev.retinalresearcher.util.Saving
import com.thejohnsondev.retinalresearcher.util.Util.rotate
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class PreviewViewModel @Inject constructor(
    private val repository: PreviewRepository,
    private val mediaManager: MediaManager
) : ViewModel() {
    private val _currentFilterOptions = MutableLiveData<Map<String, GPUImageFilter>>()
    val currentFilterOptions: LiveData<Map<String, GPUImageFilter>> get() = _currentFilterOptions

    private val _saveState = MutableLiveData<SaveState>()
    val saveState: LiveData<SaveState> = _saveState

    private var emptyFilter = GPUImageFilter()
    private var contrastFilter = GPUImageContrastFilter(DEFAULT_CONTRAST_VALUE)
    private var brightnessFilter = GPUImageBrightnessFilter()
    private var grayscaleFilter = GPUImageGrayscaleFilter()
    private var rgbFilter = GPUImageRGBFilter()
    private val saturationFilter = GPUImageSaturationFilter()
    private val whiteBalanceFilter = GPUImageWhiteBalanceFilter()

    private val filterList = mutableMapOf<String, GPUImageFilter>()

    fun addFilterOption(name: String) {
        val filerOption = when (name) {
            OPTION_CONTRAST -> contrastFilter
            OPTION_BRIGHTNESS -> brightnessFilter
            OPTION_GRAY_SCALE -> grayscaleFilter
            OPTION_SATURATION -> saturationFilter
            OPTION_WHITE_BALANCE -> whiteBalanceFilter
            OPTION_RGB -> rgbFilter
            else -> emptyFilter
        }
        filterList[name] = filerOption
        _currentFilterOptions.value = filterList
    }

    fun removeFilterOption(name: String) {
        filterList.remove(name)
        if (filterList.isEmpty()) {
            addFilterOption(OPTION_EMPTY)
        }
        _currentFilterOptions.value = filterList
    }

    fun setContrastValue(value: Float) {
        contrastFilter.setContrast(value)
        if (filterList.containsKey(OPTION_CONTRAST)) {
            filterList[OPTION_CONTRAST] = contrastFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun setBrightnessValue(value: Float) {
        brightnessFilter.setBrightness(value)
        if (filterList.containsKey(OPTION_BRIGHTNESS)) {
            filterList[OPTION_BRIGHTNESS] = brightnessFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun setSaturationValue(value: Float) {
        saturationFilter.setSaturation(value)
        if (filterList.containsKey(OPTION_SATURATION)) {
            filterList[OPTION_SATURATION] = saturationFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun setWhiteBalanceValue(value: Float) {
        whiteBalanceFilter.setTemperature(value)
        if (filterList.containsKey(OPTION_WHITE_BALANCE)) {
            filterList[OPTION_WHITE_BALANCE] = whiteBalanceFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun setRedChannelValue(value: Float) {
        rgbFilter.setRed(value)
        if (filterList.containsKey(OPTION_RGB)) {
            filterList[OPTION_RGB] = rgbFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun setGreenChannelValue(value: Float) {
        rgbFilter.setGreen(value)
        if (filterList.containsKey(OPTION_RGB)) {
            filterList[OPTION_RGB] = rgbFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun setBlueChannelValue(value: Float) {
        rgbFilter.setBlue(value)
        if (filterList.containsKey(OPTION_RGB)) {
            filterList[OPTION_RGB] = rgbFilter
            _currentFilterOptions.value = filterList
        }
    }

    fun saveImage(realTimePreview: GPUImageView, context: Context, folderName: String) {
        viewModelScope.launch(IO) {
            _saveState.postValue(Saving)
            val capture =
                realTimePreview.capture(BASE_IMAGE_SIZE, BASE_IMAGE_SIZE).rotate(BASE_PREVIEW_ROTATION)
            mediaManager.saveImage(capture, context, folderName)
            _saveState.postValue(Saved)
        }
    }

    fun isPermissionGranted(context: Context): Boolean = repository.isPermissionGranted(context)

}