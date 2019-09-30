package com.example.semakerandroid

import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpView

interface DetailsView : MvpView {
    fun hide()
    fun show()
    fun openFragment(fragment: Fragment)
    fun substractApps()
    fun addApps()
}