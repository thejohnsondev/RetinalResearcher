package com.thejohnsondev.retinalresearcher.view.preview


import android.content.Context
import androidx.lifecycle.ViewModel
import com.thejohnsondev.retinalresearcher.domain.repo.PreviewRepository
import javax.inject.Inject

class PreviewViewModel @Inject constructor(
    private val repository: PreviewRepository
) : ViewModel() {

    fun isPermissionGranted(context: Context): Boolean = repository.isPermissionGranted(context)

}