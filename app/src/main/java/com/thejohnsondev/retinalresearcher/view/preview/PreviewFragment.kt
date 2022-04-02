package com.thejohnsondev.retinalresearcher.view.preview

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thejohnsondev.retinalresearcher.R
import com.thejohnsondev.retinalresearcher.databinding.FragmentPreviewBinding
import com.thejohnsondev.retinalresearcher.util.Util.getAppComponent
import com.thejohnsondev.retinalresearcher.util.base.BaseFragment


class PreviewFragment : BaseFragment(R.layout.fragment_preview) {


    private val binding by viewBinding(FragmentPreviewBinding::bind)
    private val viewModel: PreviewViewModel by viewModels {
        getAppComponent().previewViewModelFactory()
    }

    override fun initFields() {

    }

    override fun loadData() {

    }

    override fun bindViews() {

    }

    override fun initListenersAndObservers() {

    }


}