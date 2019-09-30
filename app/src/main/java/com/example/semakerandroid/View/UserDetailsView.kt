package com.example.semakerandroid

import com.arellomobile.mvp.MvpView

interface UserDetailsView : MvpView {
    fun hide()
    fun show()
}