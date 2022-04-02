package com.thejohnsondev.retinalresearcher.util.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
        initFields()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        bindViews()
        initListenersAndObservers()
    }

    abstract fun initFields()
    abstract fun loadData()
    abstract fun bindViews()
    abstract fun initListenersAndObservers()

    protected fun onError(errorMessage: String) = Log.d("ERR", errorMessage)

}