package com.example.semakerandroid

import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpView

interface EditProfileView : MvpView {
    fun changeFragment(fragment: Fragment)
    fun hide()
    fun show()
}