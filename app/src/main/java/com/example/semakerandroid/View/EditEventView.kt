package com.example.semakerandroid

import com.arellomobile.mvp.MvpView

interface EditEventView : MvpView {
    fun uploadImage()
    fun initSpinner()
    fun hide()
    fun show()
}