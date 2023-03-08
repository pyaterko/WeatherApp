package com.example.sampleweatherapp.views.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

abstract class BaseFragment<T>(layout:Int) : Fragment(layout) {

    protected var data: T? = null
    protected lateinit var fragmentMgr: FragmentManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentMgr = (context as FragmentActivity).supportFragmentManager
    }

    protected abstract fun updateView()

    fun setValueData(data: T) {
        this.data = data
        if (isVisible) {
            updateView()
        }
    }
}