package com.thejohnsondev.retinalresearcher.view.preview


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepository
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_BRIGHTNESS
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_CONTRAST
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_EMPTY
import com.thejohnsondev.retinalresearcher.util.Const.ArgKey.OPTION_GRAY_SCALE
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.DEFAULT_BRIGHTNESS_VALUE
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.DEFAULT_CONTRAST_VALUE
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import javax.inject.Inject

class PreviewViewModel @Inject constructor(
    private val repository: PreviewRepository
) : ViewModel() {
    private val _currentFilterOptions = MutableLiveData<Map<String, GPUImageFilter>>()
    val currentFilterOptions: LiveData<Map<String, GPUImageFilter>> get() = _currentFilterOptions

    private var emptyFilter = GPUImageFilter()
    private var contrastFilter = GPUImageContrastFilter(DEFAULT_CONTRAST_VALUE)
    private var brightnessFilter = GPUImageBrightnessFilter(DEFAULT_BRIGHTNESS_VALUE)
    private var grayscaleFilter = GPUImageGrayscaleFilter()

    private val filterList = mutableMapOf<String, GPUImageFilter>()

    fun addFilterOption(name: String) {
        val filerOption = when (name) {
            OPTION_CONTRAST -> contrastFilter
            OPTION_BRIGHTNESS -> brightnessFilter
            OPTION_GRAY_SCALE -> grayscaleFilter
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

    fun isPermissionGranted(context: Context): Boolean = repository.isPermissionGranted(context)

}