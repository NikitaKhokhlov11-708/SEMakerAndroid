package com.example.semakerandroid

import com.arellomobile.mvp.MvpView

interface FeventsView : MvpView {
    fun hide()
    fun show()
    fun initRecycler()
}