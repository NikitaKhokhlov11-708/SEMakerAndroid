package com.example.semakerandroid

import com.arellomobile.mvp.MvpView
import com.example.semakerandroid.Models.Event

interface ApplicationsView : MvpView {
    fun hide()
    fun show()
    fun initializeRecycler(listEvent: ArrayList<Event>)
}