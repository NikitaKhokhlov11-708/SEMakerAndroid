package com.example.semakerandroid.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.Presenters.TeamsPresenter
import com.example.semakerandroid.R
import com.example.semakerandroid.TeamsView

class TeamsFragment : MvpAppCompatFragment(), TeamsView {

    @InjectPresenter
    lateinit var teamsPresenter: TeamsPresenter

    @ProvidePresenterTag(presenterClass = TeamsPresenter::class)
    fun provideTeamsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideTeamsPresenter() = TeamsPresenter()

    companion object {
        fun newInstance(): TeamsFragment =
            TeamsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teams, null)
    }
}
