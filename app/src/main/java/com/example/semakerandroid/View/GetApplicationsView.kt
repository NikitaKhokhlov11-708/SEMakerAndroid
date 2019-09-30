package com.example.semakerandroid

import com.arellomobile.mvp.MvpView
import com.example.semakerandroid.Models.User

interface GetApplicationsView : MvpView {
    fun hide()
    fun show()
    fun initializeRecycler(listEvents: ArrayList<User>)
}