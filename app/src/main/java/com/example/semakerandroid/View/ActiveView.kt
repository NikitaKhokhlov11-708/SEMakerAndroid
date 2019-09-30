package com.example.semakerandroid

import com.arellomobile.mvp.MvpView
import com.example.semakerandroid.Models.Event

interface ActiveView : MvpView {
    fun hide()
    fun show()
    fun initializeRecycler(listEvents: ArrayList<Event>)
}